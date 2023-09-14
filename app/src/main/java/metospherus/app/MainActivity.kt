package metospherus.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import metospherus.app.database.localhost.AppDatabase
import metospherus.app.database.profile_data.Profiles
import metospherus.app.databinding.ActivityMainBinding
import metospherus.app.modules.GeneralReminders
import metospherus.app.modules.GeneralTemplate
import metospherus.app.services.ScheduledRemindersManager
import metospherus.app.update.UpdateUtil
import metospherus.app.utilities.Constructor.insertOrUpdateUserProfile
import metospherus.app.utilities.FirebaseConfig.retrieveRealtimeDatabase
import metospherus.app.utilities.FirebaseConfig.retrieveRealtimeDatabaseOnListener
import metospherus.app.utilities.MoluccusToast
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = FirebaseDatabase.getInstance()
        appDatabase = AppDatabase.getInstance(this)

        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        metospherus = MoluccusToast(this@MainActivity)

        checkUpdate()
        startProfileDetailsListener()
        publicModulesForAllUsers()
    }

    override fun onResume() {
        super.onResume()
        initializeMedicalIntakeAlertSystem()
        startProfileDetailsListener()
        publicModulesForAllUsers()
    }

    override fun onPause() {
        super.onPause()
        initializeMedicalIntakeAlertSystem()
        stopProfileDetailsListener()
        publicModulesForAllUsers()
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
                                        val modulesData = dataSnapshot.getValue(GeneralTemplate::class.java)
                                        dataSnapshotPrivate.ref.child(currentUser.toString()).setValue(modulesData)
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
                        val userProfile = snapshot.getValue(Profiles::class.java)
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
        auth.currentUser?.let { currentUser ->
            retrieveRealtimeDatabaseOnListener(db,
                "medicalmodules/userspecific/medicineIntake/${currentUser.uid}",
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
                    Manifest.permission.INTERNET,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.WAKE_LOCK,
                    Manifest.permission.VIBRATE,
                    Manifest.permission.INSTALL_PACKAGES,
                    Manifest.permission.REQUEST_INSTALL_PACKAGES,
                    Manifest.permission.MANAGE_DEVICE_POLICY_INSTALL_UNKNOWN_SOURCES
                ).toString()
            ) != PackageManager.PERMISSION_GRANTED -> {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET,
                        Manifest.permission.POST_NOTIFICATIONS,
                        Manifest.permission.WAKE_LOCK,
                        Manifest.permission.VIBRATE,
                        Manifest.permission.INSTALL_PACKAGES,
                        Manifest.permission.REQUEST_INSTALL_PACKAGES,
                        Manifest.permission.MANAGE_DEVICE_POLICY_INSTALL_UNKNOWN_SOURCES
                    ),
                    PERMISSIONS_REQUEST_CODE
                )
                return
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // TODO: Remove
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

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}