package metospherus.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorEventListener2
import android.hardware.SensorListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import metospherus.app.database.localhost.AppDatabase
import metospherus.app.database.profile_data.GeneralUserInformation
import metospherus.app.databinding.ActivityMainBinding
import metospherus.app.modules.GeneralReminders
import metospherus.app.modules.GeneralTemplate
import metospherus.app.services.NetworkHandler
import metospherus.app.services.ScheduledRemindersManager
import metospherus.app.update.UpdateUtil
import metospherus.app.utilities.Constructor.getDeviceUsageHoursToday
import metospherus.app.utilities.Constructor.insertOrUpdateUserProfile
import metospherus.app.utilities.FirebaseConfig
import metospherus.app.utilities.FirebaseConfig.retrieveRealtimeDatabase
import metospherus.app.utilities.FirebaseConfig.retrieveRealtimeDatabaseOnListener
import metospherus.app.utilities.MoluccusToast
import java.text.SimpleDateFormat
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() , SensorEventListener {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var appDatabase: AppDatabase

    private lateinit var preferences: SharedPreferences
    private var profileDetailsListener: ValueEventListener? = null
    private val PERMISSIONS_REQUEST_CODE = 1001

    private lateinit var metospherus: MoluccusToast

    private lateinit var sensorManager: SensorManager
    private lateinit var stepCounterSensor: Sensor
    private var stepCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = FirebaseDatabase.getInstance()
        appDatabase = AppDatabase.getInstance(this)
        val networkHandler = NetworkHandler(this)

        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        metospherus = MoluccusToast(this@MainActivity)

        checkUpdate()
        startProfileDetailsListener()
        publicModulesForAllUsers()

        if (auth.currentUser?.uid != null) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    db.getReference("MedicalMessenger/FcmTokens/${auth.currentUser?.uid}")
                        .setValue(task.result.toString())
                } else {
                    metospherus.showError("Failed to assign PnT token Reason : ${task.exception?.message}")
                }
            }
        }

        when {
            networkHandler.isOnline() -> {
                // Device is online
            }

            else -> {
                // Device is offline
            }
        }
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)!!

        if (this.stepCounterSensor == null) {
            auth.currentUser?.uid?.let { userId ->
                FirebaseConfig.updateRealtimeDatabaseData(
                    db,
                    "participants/$userId/generalHealthInformation/stepsRecord",
                    "Sensor not available"
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initializeMedicalIntakeAlertSystem()
        startProfileDetailsListener()
        publicModulesForAllUsers()
        stepCount = 0
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        initializeMedicalIntakeAlertSystem()
        stopProfileDetailsListener()
        publicModulesForAllUsers()
        sensorManager.unregisterListener(this)
    }

    private fun publicModulesForAllUsers() {
        auth.currentUser?.uid.let { currentUser ->
            retrieveRealtimeDatabaseOnListener(
                db,
                "medicalmodules/modules",
                this,
                onDataChange = { snapshot ->
                    for (dataSnapshot in snapshot.children) {
                        val key = dataSnapshot.key
                        if (key != null) {
                            retrieveRealtimeDatabaseOnListener(
                                db,
                                "medicalmodules/userspecific/modules/$currentUser/$key",
                                this,
                                onDataChange = { dataSnapshotPrivate ->
                                    if (!dataSnapshotPrivate.exists()) {
                                        val modulesData =
                                            dataSnapshot.getValue(GeneralTemplate::class.java)
                                        dataSnapshotPrivate.ref.child(currentUser.toString())
                                            .setValue(modulesData)
                                    }
                                })
                        }
                    }
                })
        }
    }

    private fun startProfileDetailsListener() {
        if (profileDetailsListener == null) {
            auth.currentUser?.uid?.let { userId ->
                retrieveRealtimeDatabaseOnListener(db, "participants/$userId", this,
                    onDataChange = { snapshot ->
                        val userProfile = snapshot.getValue(GeneralUserInformation::class.java)
                        if (userProfile != null) {
                            CoroutineScope(Dispatchers.Default).launch {
                                insertOrUpdateUserProfile(userProfile, appDatabase)
                            }
                        }
                    })
            }
        }
    }

    private fun stopProfileDetailsListener() {
        profileDetailsListener?.let { listener ->
            auth.currentUser?.uid?.let { userId ->
                retrieveRealtimeDatabase(db, "participants/$userId").removeEventListener(listener)
            }
            profileDetailsListener = null
        }
    }

    private fun initializeMedicalIntakeAlertSystem() {
        auth.currentUser?.uid.let { currentUser ->
            val deviceUsg = getDeviceUsageHoursToday(this)
            FirebaseConfig.updateRealtimeDatabaseData(
                db,
                "participants/$currentUser/generalHealthInformation/deviceUsageTimeRecord",
                deviceUsg.toString()
            )

            retrieveRealtimeDatabaseOnListener(db,
                "medicalmodules/userspecific/medicineIntake/${currentUser}",
                context = this,
                onDataChange = { snapshot ->
                    val scheduledReminders = mutableListOf<GeneralReminders>()
                    for (snapshotSchedule in snapshot.children) {
                        val schTime =
                            snapshotSchedule.child("medicineTime").getValue(String::class.java)
                        val schDate =
                            snapshotSchedule.child("medicineDate").getValue(String::class.java)
                        val schTitle =
                            snapshotSchedule.child("medicineName").getValue(String::class.java)

                        if (!schTime.isNullOrEmpty() && !schDate.isNullOrEmpty() && !schTitle.isNullOrEmpty()) {
                            val scheduledList = GeneralReminders(schTime, schDate, schTitle)
                            scheduledReminders.add(scheduledList)
                        }
                    }
                    ScheduledRemindersManager(this@MainActivity).scheduleNotifications(
                        scheduledReminders
                    )
                })
        }
    }

    @SuppressLint("InlinedApi")
    override fun onStart() {
        super.onStart()
        when {
            ContextCompat.checkSelfPermission(
                this,
                arrayListOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.WAKE_LOCK,
                    Manifest.permission.VIBRATE,
                    Manifest.permission.ACTIVITY_RECOGNITION,
                    Manifest.permission.REQUEST_INSTALL_PACKAGES,
                ).toString()
            ) != PackageManager.PERMISSION_GRANTED -> {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.POST_NOTIFICATIONS,
                        Manifest.permission.WAKE_LOCK,
                        Manifest.permission.VIBRATE,
                        Manifest.permission.ACTIVITY_RECOGNITION,
                        Manifest.permission.REQUEST_INSTALL_PACKAGES
                    ),
                    PERMISSIONS_REQUEST_CODE
                )
                return
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (i in permissions.indices) {
            if (permissions.contains(Manifest.permission.POST_NOTIFICATIONS)) continue
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                createPermissionRequestDialog()
            }
        }
    }

    private fun createPermissionRequestDialog() {
        val dialog = MaterialAlertDialogBuilder(this)
        dialog.setTitle(getString(R.string.warning))
        dialog.setMessage(getString(R.string.request_permission_desc))
        dialog.setOnCancelListener { exitProcess(0) }
        dialog.setNegativeButton(getString(R.string.exit_app)) { _: DialogInterface?, _: Int ->
            exitProcess(
                0
            )
        }
        dialog.setPositiveButton(getString(R.string.ok)) { _: DialogInterface?, _: Int ->
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null)
            )
            startActivity(intent)
            exitProcess(0)
        }
        dialog.show()
    }

    private fun checkUpdate() {
        if (preferences.getBoolean("update_app", true)) {
            val updateUtil = UpdateUtil(this)
            lifecycleScope.launch(Dispatchers.IO) {
                updateUtil.updateApp {}
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onSensorChanged(p0: SensorEvent?) {
        p0 ?: return

        val lastDeviceBootTimeInMillis = System.currentTimeMillis() - SystemClock.elapsedRealtime()
        val sensorEventTimeInNanos = p0.timestamp // The number of nanosecond passed since the time of last boot
        val sensorEventTimeInMillis = sensorEventTimeInNanos / 1000_000

        val actualSensorEventTimeInMillis = lastDeviceBootTimeInMillis + sensorEventTimeInMillis
        val displayDateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(actualSensorEventTimeInMillis)
        println("Sensor event is triggered at $displayDateStr")

        p0.values.firstOrNull()?.let {
            auth.currentUser?.uid?.let { userId ->
                FirebaseConfig.updateRealtimeDatabaseData(
                    db,
                    "participants/$userId/generalHealthInformation/stepsRecord",
                    it.toString()
                )
            }
        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // Not needed for the Step Counter Sensor
    }

    /** override fun onSupportNavigateUp(): Boolean {
    val navController = findNavController(R.id.nav_host_fragment_content_main)
    return navController.navigateUp(appBarConfiguration)
    || super.onSupportNavigateUp()
    }
     **/
}