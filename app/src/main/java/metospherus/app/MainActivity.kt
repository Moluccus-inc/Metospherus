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
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.preference.PreferenceManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import metospherus.app.databinding.ActivityMainBinding
import metospherus.app.update.UpdateUtil
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

    private lateinit var preferences: SharedPreferences

    private val PERMISSIONS_REQUEST_CODE = 1001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = FirebaseDatabase.getInstance()

        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        checkUpdate()
    }

    @SuppressLint("InlinedApi")
    override fun onStart() {
        super.onStart()
        when {
            auth.currentUser != null -> {
                val usrDb = db.getReference("participants").child(auth.currentUser!!.uid)
                usrDb.keepSynced(true)

                usrDb.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        when {
                            !dataSnapshot.hasChild("userId") -> {
                                initCompleteUserProfile(usrDb)
                            }
                            else -> {
                                val startTimestamp = System.currentTimeMillis() / 1000 // Get current timestamp in seconds
                                val dateFormat = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault())
                                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

                                val exitDate = dateFormat.format(startTimestamp * 1000) // Convert to milliseconds
                                val exitTime = timeFormat.format(startTimestamp * 1000) // Convert to milliseconds

                                val activeStatus = mapOf(
                                    "active_status" to true,
                                    "active_time" to "$exitTime - $exitDate",
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
                    Manifest.permission.VIBRATE
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
                        Manifest.permission.VIBRATE
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
                                    "active_status" to false,
                                    "active_time" to "$exitTime - $exitDate",
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
        dialog.setNegativeButton(getString(R.string.exit_app)) { _: DialogInterface?, _: Int -> exitProcess(0) }
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

    private fun initCompleteUserProfile(usrDb: DatabaseReference) {
        MaterialDialog(this).show {
            customView(R.layout.activity_complete_user_profile)
            cornerRadius(literalDp = 20f)

            val accountType = view.findViewById<AutoCompleteTextView>(R.id.accountType)
            val userPreferedName = view.findViewById<TextInputEditText>(R.id.userPreferedName)
            val userHandle = view.findViewById<TextInputEditText>(R.id.userHandle)
            val userId = view.findViewById<TextInputEditText>(R.id.userId)
            val usersEmail = view.findViewById<TextInputEditText>(R.id.usersEmail)
            val usersEmailLayout = view.findViewById<TextInputLayout>(R.id.usersEmailLayout)

            onShow {
                val displayMetrics = windowContext.resources.displayMetrics
                val dialogWidth =
                    displayMetrics.widthPixels - (2 * windowContext.resources.getDimensionPixelSize(
                        R.dimen.dialog_margin_horizontal
                    ))
                window?.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
            }

            val accountTypes = resources.getStringArray(R.array.metospherus_account_type)
            val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_dropdown_item_1line, accountTypes)
            accountType.setAdapter(adapter)

            accountType.setOnItemClickListener { parent, _, position, _ ->
                val selectedAccountType = parent.getItemAtPosition(position) as String
                val accountTypeHolder = selectedAccountType.trim().replace(" ", "_").lowercase()
                val accountTypePrefix = accountTypeHolder.take(3).uppercase(Locale.ROOT)
                val uidSubstring = auth.currentUser?.uid
                val uidDigits = uidSubstring?.hashCode()?.toString()?.replace("-", "")?.take(12)
                val userIdentification = "$accountTypePrefix$uidDigits"
                userId.setText(userIdentification)
            }


            userHandle.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(editable: Editable?) {
                    val input = editable.toString()

                    val validInput = input.replace(" ", "_") // Replace spaces with underscores
                        .replace(".", "_") // Replace dots with underscores
                        .replace(Regex("[^a-zA-Z0-9_]"), "") // Remove all characters except letters, digits, and underscore

                    // Update the EditText with the valid input
                    if (input != validInput) {
                        editable?.replace(0, editable.length, validInput)
                    }
                }
            })

            usersEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    // Not needed for your implementation
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    // Not needed for your implementation
                }

                override fun afterTextChanged(editable: Editable?) {
                    val input = editable.toString()

                    if (isValidEmail(input)) {
                        // Email is valid, remove any previous error message if shown
                        usersEmailLayout.error = null
                    } else {
                        // Show an error message to the user indicating that the email format is incorrect
                        usersEmailLayout.error = "Invalid email format"
                    }
                }
            })

            positiveButton(text = "Complete") {
                val selectedAccountType = accountType.text.toString().trim()
                val accountTypeHolder = selectedAccountType.replace(" ", "_").lowercase()
                val accountTypePrefix = accountTypeHolder.take(3).uppercase(Locale.ROOT)

                val handle = userHandle.text?.trim().toString().replace(" ", "_").lowercase()
                val email = usersEmail.text?.trim().toString()

                when {
                    selectedAccountType.isEmpty() || userPreferedName.text!!.isEmpty() || handle.isEmpty() -> {
                        // Show specific error messages for each field if they are empty
                    }
                    !isValidEmail(email) -> {
                        // Show an error message for invalid email format
                        usersEmailLayout.error = "Invalid email format"
                    }
                    else -> {
                        val uidSubstring = auth.currentUser?.uid
                        val uidDigits = uidSubstring?.hashCode()?.toString()?.replace("-", "")?.take(12)
                        val userIdentification = "$accountTypePrefix$uidDigits"

                        val addProfileDetails = mapOf(
                            "accountType" to accountTypeHolder,
                            "handle" to "@$handle",
                            "userId" to userIdentification,
                            "email" to email,
                            "name" to userPreferedName.text.toString()
                        )

                        usrDb.updateChildren(addProfileDetails)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    dismiss()
                                    MoluccusToast(this@MainActivity).showSuccess("Profile updated successfully!!\uD83D\uDE0A")
                                } else {
                                    MoluccusToast(this@MainActivity).showError("Error: ${task.exception?.message}")
                                }
                            }
                    }
                }
            }
        }
    }
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }
    private fun checkUpdate() {
        if (preferences.getBoolean("update_app", true)) {
            val updateUtil = UpdateUtil(this)
            lifecycleScope.launch(Dispatchers.IO){
                updateUtil.updateApp{}
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}