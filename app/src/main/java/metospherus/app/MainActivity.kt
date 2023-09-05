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
import androidx.fragment.app.FragmentManager
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
import kotlinx.coroutines.withContext
import metospherus.app.database.localhost.AppDatabase
import metospherus.app.database.profile_data.Profiles
import metospherus.app.databinding.ActivityMainBinding
import metospherus.app.modules.GeneralReminders
import metospherus.app.modules.GeneralTemplate
import metospherus.app.services.ScheduledRemindersManager
import metospherus.app.update.UpdateUtil
import metospherus.app.utilities.Constructor
import metospherus.app.utilities.Constructor.insertOrUpdateUserProfile
import metospherus.app.utilities.MoluccusToast
import java.text.SimpleDateFormat
import java.util.Locale
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
        auth.currentUser?.let { currentUser ->
            val patientGeneralModulesDB = db.getReference("medicalmodules").child("modules")
            val patientPrivateGeneralModulesDB =
                db.getReference("medicalmodules").child("userspecific").child("modules")
                    .child(currentUser.uid)

            patientGeneralModulesDB.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnapshot in snapshot.children) {
                        val key = dataSnapshot.key
                        if (key != null) {
                            patientPrivateGeneralModulesDB.child(key)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshotPrivate: DataSnapshot) {
                                        if (!dataSnapshotPrivate.exists()) {
                                            val modulesData =
                                                dataSnapshot.getValue(GeneralTemplate::class.java)
                                            patientPrivateGeneralModulesDB.child(key)
                                                .setValue(modulesData)
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {}
                                })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }
    private fun startProfileDetailsListener() {
        if (profileDetailsListener == null) {
            auth.currentUser?.uid?.let { userId ->
                val profileDetails = db.getReference("participants").child(userId)
                profileDetails.keepSynced(true)
                profileDetailsListener = profileDetails.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userProfile = snapshot.getValue(Profiles::class.java)
                        if (userProfile != null) {
                            CoroutineScope(Dispatchers.Default).launch {
                                insertOrUpdateUserProfile(userProfile, appDatabase)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        MoluccusToast(this@MainActivity).showError("Cancelled because ${error.message}")
                    }
                })
            }
        }
    }

    private fun stopProfileDetailsListener() {
        profileDetailsListener?.let { listener ->
            auth.currentUser?.uid?.let { userId ->
                val profileDetails = db.getReference("participants").child(userId)
                profileDetails.removeEventListener(listener)
            }
            profileDetailsListener = null
        }
    }
    private fun initializeMedicalIntakeAlertSystem() {
        auth.currentUser?.let { currentUser ->
            val schedulingReferences = db.getReference("medicalmodules")
                .child("userspecific")
                .child("medicineIntake")
                .child(currentUser.uid)
            schedulingReferences.keepSynced(true)
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val scheduledReminders = mutableListOf<GeneralReminders>()
                    for (snapshotSchedule in snapshot.children) {
                        val schTime = snapshotSchedule.child("medicineTime").getValue(String::class.java)
                        val schDate = snapshotSchedule.child("medicineDate").getValue(String::class.java)
                        val schTitle = snapshotSchedule.child("medicineName").getValue(String::class.java)

                        if (!schTime.isNullOrEmpty() && !schDate.isNullOrEmpty() && !schTitle.isNullOrEmpty()) {
                            val scheduledList = GeneralReminders(schTime, schDate, schTitle)
                            scheduledReminders.add(scheduledList)
                        }
                    }
                    ScheduledRemindersManager(this@MainActivity).scheduleNotifications(
                        scheduledReminders
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    MoluccusToast(this@MainActivity).showError("Cancelled ${error.message}")
                }
            }
            schedulingReferences.addValueEventListener(valueEventListener)
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
        when {
            auth.currentUser != null -> {
                val usrDb = db.getReference("participants").child(auth.currentUser!!.uid)
                usrDb.keepSynced(true)

                usrDb.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        when {
                            !dataSnapshot.hasChild("userId") -> {
                                // initCompleteUserProfile(usrDb)
                            }

                            else -> {
                                val startTimestamp = System.currentTimeMillis() / 1000 // Get current timestamp in seconds
                                val dateFormat = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault())
                                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

                                val exitDate = dateFormat.format(startTimestamp * 1000) // Convert to milliseconds
                                val exitTime = timeFormat.format(startTimestamp * 1000) // Convert to milliseconds
                                val activeStatus = mapOf(
                                    "generalSystemInformation" to mapOf(
                                        "activeLogStatus" to false,
                                        "activeTimeStatus" to "$exitTime - $exitDate",
                                    )
                                )
                                usrDb.updateChildren(activeStatus)
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle the error if needed.
                    }
                })
            }

            else -> {
                // do nothing
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

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}