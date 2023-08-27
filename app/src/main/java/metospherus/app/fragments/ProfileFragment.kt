package metospherus.app.fragments

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.bumptech.glide.Glide
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import metospherus.app.R
import metospherus.app.database.localhost.AppDatabase
import metospherus.app.database.profile_data.Profiles
import metospherus.app.databinding.FragmentProfileBinding
import metospherus.app.utilities.Constructor.getUserProfilesFromDatabase
import metospherus.app.utilities.MoluccusToast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var appDatabase: AppDatabase

    private lateinit var imgAvatar: ShapeableImageView
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                launchImagePicker(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        auth.useAppLanguage()
        db = FirebaseDatabase.getInstance()
        appDatabase = AppDatabase.getInstance(requireContext())

        binding.backHolderKey.setOnClickListener {
            findNavController().navigate(R.id.action_home)
        }

        binding.editProfile.setOnClickListener {
            initProfileEditingIfNeeded()
        }

        initProfileDetailsIfNeeded()
    }

    suspend fun insertOrUpdateUserProfile(userProfile: Profiles) {
        withContext(Dispatchers.IO) {
            appDatabase.profileLocal().insertOrUpdateUserPatient(userProfile)
        }
    }

    private fun initProfileDetailsIfNeeded() {
        val dob = binding.dob
        val userIdentification = binding.identifier
        val userAddress = binding.address
        val userName = binding.name
        val userEmail = binding.email
        val userPhone = binding.phoneNumber
        val userHandle = binding.handle
        val userAvatar = binding.avatar
        val accountTypes = binding.accountTypes

        auth.currentUser?.uid?.let { userId ->
            val profileDetails = db.getReference("participants").child(userId)
            profileDetails.keepSynced(true)
            profileDetails.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userProfile = snapshot.getValue(Profiles::class.java)
                    if (userProfile != null) {
                        lifecycleScope.launch {
                            insertOrUpdateUserProfile(userProfile)
                        }
                    }

                    userName.text = snapshot.child("name").getValue(String::class.java) ?: "Unknown"
                    userPhone.text = snapshot.child("phoneNumber").getValue(String::class.java) ?: "Unknown"
                    userHandle.text = snapshot.child("handle").getValue(String::class.java) ?: "Unknown"
                    userEmail.text = snapshot.child("email").getValue(String::class.java) ?: "Unknown@email.com"
                    userAddress.text = snapshot.child("address").getValue(String::class.java) ?: "Unknown address"
                    userIdentification.text = snapshot.child("userId").getValue(String::class.java) ?: "Unknown Id"
                    dob.text = snapshot.child("dob").getValue(String::class.java) ?: "Unknown"
                    accountTypes.text = snapshot.child("accountType").getValue(String::class.java) ?: "Unknown"

                    Glide.with(requireContext())
                        .load(snapshot.child("avatar").getValue(String::class.java))
                        .centerCrop()
                        .placeholder(R.drawable.holder)
                        .into(userAvatar)
                }

                override fun onCancelled(error: DatabaseError) {
                    MoluccusToast(requireContext()).showError("Cancelled because ${error.message}")
                }
            })
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initProfileEditingIfNeeded() {
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            customView(R.layout.profileinstance_layout)
            cancelOnTouchOutside(false)
            cornerRadius(20f)

            val closeBottomSheetProfile = view.findViewById<ImageView>(R.id.CloseBottomSheetProfile)
            val preferredNameInput = view.findViewById<TextInputEditText>(R.id.preferredNameInput)
            val genderIndentityInput = view.findViewById<TextInputEditText>(R.id.genderIndentityInput)
            val primaryLocationInput = view.findViewById<TextInputEditText>(R.id.primaryLocationInput)
            val phoneNumberInput = view.findViewById<TextInputEditText>(R.id.phoneNumberInput)

            val fullLegalNameInput = view.findViewById<TextInputEditText>(R.id.fullLegalNameInput)
            val emailAddressInput = view.findViewById<TextInputEditText>(R.id.emailAddressInput)
            val userHeightInput = view.findViewById<TextInputEditText>(R.id.userHeightInput)

            val dateOfBirthInput = view.findViewById<TextInputEditText>(R.id.dateOfBirthInput)
            val faEnableCheckBox = view.findViewById<MaterialCheckBox>(R.id.faEnableCheckBox)

            imgAvatar = view.findViewById(R.id.imgAvatar)
            val imgUpload = view.findViewById<TextView>(R.id.imgUpload)

            faEnableCheckBox.setOnCheckedChangeListener { compoundButton, b ->
                // to do
            }


            imgUpload.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            primaryLocationInput.setOnClickListener {
                fetchAddress(primaryLocationInput)
            }
            preferredNameInput.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    p0.let {
                        if (it?.isNotEmpty() == true) {
                            val inputs = it.toString().trim()
                            updateFirebaseDatabase("name", inputs)
                        }
                    }
                }
            })
            genderIndentityInput.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    p0.let {
                        if (it?.isNotEmpty() == true) {
                            val inputs = it.toString().trim()
                            updateFirebaseDatabase("gender", inputs)
                        }
                    }
                }
            })
            primaryLocationInput.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    p0.let {
                        if (it?.isNotEmpty() == true) {
                            val inputs = it.toString().trim()
                            updateFirebaseDatabase("address", inputs)
                        }
                    }
                }
            })
            fullLegalNameInput.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    p0.let {
                        if (it?.isNotEmpty() == true) {
                            val inputs = it.toString().trim()
                            updateFirebaseDatabase("legalName", inputs)
                        }
                    }
                }
            })
            userHeightInput.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    p0.let {
                        if (it?.isNotEmpty() == true) {
                            val inputs = it.toString().trim()
                            updateFirebaseDatabase("height", inputs)
                        }
                    }
                }
            })
            dateOfBirthInput.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val builder = MaterialDatePicker.Builder.datePicker()
                    builder.setTitleText("Select Date of Birth")

                    val today = Calendar.getInstance()

                    val minCalendar = Calendar.getInstance()
                    minCalendar.set(today.get(Calendar.YEAR) - 120, 0, 1) // 120 years ago
                    builder.setSelection(minCalendar.timeInMillis)

                    val existingDate = dateOfBirthInput.text.toString()
                    if (existingDate.isNotEmpty()) {
                        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
                        val parsedDate = dateFormat.parse(existingDate)

                        val selectedCalendar = Calendar.getInstance()
                        selectedCalendar.time = parsedDate
                        builder.setSelection(selectedCalendar.timeInMillis)
                    }

                    val datePicker = builder.build()
                    datePicker.addOnPositiveButtonClickListener { selectedDateInMillis ->
                        val selectedCalendar = Calendar.getInstance()
                        selectedCalendar.timeInMillis = selectedDateInMillis

                        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
                        val formattedDate = dateFormat.format(selectedCalendar.time)

                        updateFirebaseDatabase("dob", formattedDate.toString())
                        dateOfBirthInput.setText(formattedDate)
                    }

                    datePicker.show(requireActivity().supportFragmentManager, "datePicker")
                }
                true
            }

            lifecycleScope.launch {
                val userPatient = getUserProfilesFromDatabase(appDatabase)
                if (userPatient != null) {
                    preferredNameInput.setText(userPatient.name)
                    genderIndentityInput.setText(userPatient.gender)
                    primaryLocationInput.setText(userPatient.address)
                    phoneNumberInput.setText(userPatient.phoneNumber)
                    fullLegalNameInput.setText(userPatient.legalName)
                    emailAddressInput.setText(userPatient.email)
                    dateOfBirthInput.setText(userPatient.dob)
                    Glide.with(requireContext())
                        .load(userPatient.avatar)
                        .placeholder(R.drawable.holder)
                        .into(imgAvatar)
                }
            }
            closeBottomSheetProfile.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun updateFirebaseDatabase(id: String, value: String) {
        if (value != "null" && value != null && value != "Not Set") {
            val uid = auth.uid
            val databaseRef = db.getReference("participants/$uid")
            databaseRef.child(id).setValue(value)
        }
    }

    private fun fetchAddress(homeAddress: TextInputEditText) {
        val latitude = 0.0
        val longitude = 0.0
        val geocoder = Geocoder(requireActivity(), Locale.getDefault())
        try {
            val addresses: List<Address> =
                geocoder.getFromLocation(latitude, longitude, 1) as List<Address>
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                val formattedAddress = address.getAddressLine(0)
                homeAddress.setText(formattedAddress)
            }
        } catch (e: Exception) {
            MoluccusToast(requireContext()).showError("Failed to fetch address ${e.message}")
        }
    }

    private fun launchImagePicker(uri: Uri) {
        val imageUri: Uri = uri
        imgAvatar.setImageURI(imageUri)

        val uid = auth.currentUser?.uid
        if (uid != null) {
            val storageRef = FirebaseStorage.getInstance().reference
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(imageUri.toString())
            val imageRef = storageRef.child("participants/profile_images/$uid/${"avatar"}.$fileExtension")
            val uploadTask = imageRef.putFile(imageUri)
            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val userRef = db.getReference("participants/$uid")
                    userRef.child("avatar").setValue(uri.toString())
                }.addOnFailureListener { exception ->
                    Log.e("Download URL Error", exception.message.toString())
                    MoluccusToast(requireContext()).showInformation("Download URL Error ${exception.message.toString()}")
                }
            }.addOnFailureListener { exception ->
                Log.e("Upload Error", exception.message.toString())
                MoluccusToast(requireContext()).showInformation("Upload Error ${exception.message.toString()}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}