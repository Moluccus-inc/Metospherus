package metospherus.app.fragments

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.callbacks.onPreShow
import com.afollestad.materialdialogs.customview.customView
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import metospherus.app.R
import metospherus.app.database.localhost.AppDatabase
import metospherus.app.database.profile_data.GeneralUserInformation
import metospherus.app.databinding.FragmentProfileBinding
import metospherus.app.utilities.Constructor
import metospherus.app.utilities.Constructor.checkLocationPermission
import metospherus.app.utilities.Constructor.getApproximateLocation
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

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var profileDetailsListener: ValueEventListener? = null
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

    @RequiresApi(Build.VERSION_CODES.P)
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
        startProfileDetailsListener()

        val hasLocationPermission = checkLocationPermission(requireActivity())
        if (hasLocationPermission) {
            requestLocationUpdates()
        } else {
            MoluccusToast(requireContext()).showInformation("Location permission denied")
        }
    }

    override fun onResume() {
        super.onResume()
        startProfileDetailsListener()
        initProfileDetailsIfNeeded()
    }

    override fun onPause() {
        super.onPause()
        stopProfileDetailsListener()
    }

    private fun startProfileDetailsListener() {
        if (profileDetailsListener == null) {
            auth.currentUser?.uid?.let { userId ->
                val profileDetails = db.getReference("participants").child(userId)
                profileDetails.keepSynced(true)
                profileDetailsListener =
                    profileDetails.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val userProfile = snapshot.getValue(GeneralUserInformation::class.java)
                            if (userProfile != null) {
                                CoroutineScope(Dispatchers.Default).launch {
                                    Constructor.insertOrUpdateUserProfile(userProfile, appDatabase)
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            MoluccusToast(requireContext()).showError("Cancelled because ${error.message}")
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

    private fun initProfileDetailsIfNeeded() {
        CoroutineScope(Dispatchers.Main).launch {
            val livePatientDetails = getUserProfilesFromDatabase(appDatabase)
            livePatientDetails?.let { userPatient ->
                binding.name.text = userPatient.generalDescription.usrPreferedName ?: "Unknown"
                binding.handle.text = userPatient.generalDescription.usrDistinguishedHandle ?: "Unknown"
                binding.address.text = userPatient.generalDescription.physicalAddress ?: "Unknown Address"
                binding.phoneNumber.text = userPatient.generalDescription.usrPrimaryPhone ?: "Unknown"
                binding.accountTypes.text = userPatient.accountType ?: "Unknown Type"
                binding.email.text = userPatient.generalDescription.usrPrimaryEmail ?: "Unknown@email.com"
                binding.dob.text = userPatient.generalDescription.usrDateOfBirth ?: "Unknown"
                binding.identifier.text = userPatient.generalDatabaseInformation.userGeneralIdentificationNumber ?: "Unknown"

                binding.profileWeightTv.text = userPatient.generalHealthInformation.weightRecord ?: "Unknown"
                binding.profileAllergiesTv.text = userPatient.generalHealthInformation.allergiesRecord ?: "Unknown"
                binding.bloodGroupTv.text = userPatient.generalHealthInformation.bloodGroupRecord ?: "Unknown"
                binding.physicalHeightTv.text = userPatient.generalHealthInformation.heightRecord ?: "Unknown"

                Glide.with(requireContext())
                    .load(userPatient.avatar)
                    .placeholder(R.drawable.holder)
                    .into(binding.avatar)
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("ClickableViewAccessibility")
    private fun initProfileEditingIfNeeded() {
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            customView(R.layout.profileinstance_layout)
            cancelOnTouchOutside(false)
            cornerRadius(20f)

            val profileLayout = view.findViewById<LinearLayout>(R.id.profileLayout)
            val closeBottomSheetProfile = view.findViewById<ImageView>(R.id.CloseBottomSheetProfile)
            val preferredNameInput = view.findViewById<TextInputEditText>(R.id.preferredNameInput)
            val genderIndentityInput = view.findViewById<TextInputEditText>(R.id.genderIndentityInput)
            val primaryLocationInput = view.findViewById<TextInputEditText>(R.id.primaryLocationInput)
            val currentCountryInput = view.findViewById<TextInputEditText>(R.id.currentCountryInput)
            val phoneNumberInput = view.findViewById<TextInputEditText>(R.id.phoneNumberInput)

            val fullLegalNameInput = view.findViewById<TextInputEditText>(R.id.fullLegalNameInput)
            val emailAddressInput = view.findViewById<TextInputEditText>(R.id.emailAddressInput)

            val profileAllergiesTextEdit = view.findViewById<TextInputEditText>(R.id.profileAllergiesTextEdit)
            val profileBloodGroupTextEdit = view.findViewById<TextInputEditText>(R.id.profileBloodGroupTextEdit)
            val profileHeightTextEdit = view.findViewById<TextInputEditText>(R.id.profileHeightTextEdit)
            val profileWeightTextEdit = view.findViewById<TextInputEditText>(R.id.profileWeightTextEdit)

            val dateOfBirthInput = view.findViewById<TextInputEditText>(R.id.dateOfBirthInput)

            imgAvatar = view.findViewById(R.id.imgAvatar)
            val imgUpload = view.findViewById<TextView>(R.id.imgUpload)

            imgUpload.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            primaryLocationInput.setOnClickListener {
                fetchAddress(primaryLocationInput)
            }

            phoneNumberInput.doAfterTextChanged { p0 ->
                p0.let {
                    if (it?.isNotEmpty() == true) {
                        val inputs = it.toString().trim()
                        updateFirebaseDatabase("generalDescription/usrPrimaryPhone", inputs)
                    }
                }
            }
            emailAddressInput.doAfterTextChanged { p0 ->
                p0.let {
                    if (it?.isNotEmpty() == true) {
                        val inputs = it.toString().trim()
                        updateFirebaseDatabase("generalDescription/usrPrimaryEmail", inputs)
                    }
                }
            }
            profileAllergiesTextEdit.doAfterTextChanged { p0 ->
                p0.let {
                    if (it?.isNotEmpty() == true) {
                        val inputs = it.toString().trim()
                        updateFirebaseDatabase("generalHealthInformation/allergiesRecord", inputs)
                    }
                }
            }
            profileBloodGroupTextEdit.doAfterTextChanged { p0 ->
                p0.let {
                    if (it?.isNotEmpty() == true) {
                        val inputs = it.toString().trim()
                        updateFirebaseDatabase("generalHealthInformation/bloodGroupRecord", inputs)
                    }
                }
            }
            profileHeightTextEdit.doAfterTextChanged { p0 ->
                p0.let {
                    if (it?.isNotEmpty() == true) {
                        val inputs = it.toString().trim()
                        updateFirebaseDatabase("generalHealthInformation/heightRecord", inputs)
                    }
                }
            }
            profileWeightTextEdit.doAfterTextChanged { p0 ->
                p0.let {
                    if (it?.isNotEmpty() == true) {
                        val inputs = it.toString().trim()
                        updateFirebaseDatabase("generalHealthInformation/weightRecord", inputs)
                    }
                }
            }
            preferredNameInput.doAfterTextChanged { p0 ->
                p0.let {
                    if (it?.isNotEmpty() == true) {
                        val inputs = it.toString().trim()
                        updateFirebaseDatabase("generalDescription/usrPreferedName", inputs)
                    }
                }
            }
            genderIndentityInput.doAfterTextChanged { p0 ->
                p0.let {
                    if (it?.isNotEmpty() == true) {
                        val inputs = it.toString().trim()
                        updateFirebaseDatabase("generalHealthInformation/genderIdRecord", inputs)
                    }
                }
            }
            primaryLocationInput.doAfterTextChanged { p0 ->
                p0.let {
                    if (it?.isNotEmpty() == true) {
                        val inputs = it.toString().trim()
                        updateFirebaseDatabase("generalDescription/physicalAddress", inputs)
                    }
                }
            }
            fullLegalNameInput.doAfterTextChanged { p0 ->
                p0.let {
                    if (it?.isNotEmpty() == true) {
                        val inputs = it.toString().trim()
                        updateFirebaseDatabase("generalDescription/usrFullLegalName", inputs)
                    }
                }
            }
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
                        if (parsedDate != null) {
                            selectedCalendar.time = parsedDate
                        }
                        builder.setSelection(selectedCalendar.timeInMillis)
                    }

                    val datePicker = builder.build()
                    datePicker.addOnPositiveButtonClickListener { selectedDateInMillis ->
                        val selectedCalendar = Calendar.getInstance()
                        selectedCalendar.timeInMillis = selectedDateInMillis

                        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
                        val formattedDate = dateFormat.format(selectedCalendar.time)

                        updateFirebaseDatabase(
                            "generalDescription/usrDateOfBirth",
                            formattedDate.toString()
                        )
                        dateOfBirthInput.setText(formattedDate)
                    }

                    datePicker.show(requireActivity().supportFragmentManager, "datePicker")
                }
                true
            }

            onPreShow {
                profileLayout.loadSkeleton {
                    val customShimmer = Shimmer.AlphaHighlightBuilder()
                        .setDirection(Shimmer.Direction.TOP_TO_BOTTOM)
                        .build()
                    shimmer(customShimmer)
                }
                lifecycleScope.launch {
                    val livePatientDetails = getUserProfilesFromDatabase(appDatabase)
                    livePatientDetails?.let { userPatient ->
                        profileLayout.hideSkeleton()

                        preferredNameInput.setText(userPatient.generalDescription.usrPreferedName)
                        genderIndentityInput.setText(userPatient.generalHealthInformation.genderIdRecord)
                        primaryLocationInput.setText(userPatient.generalDescription.physicalAddress)
                        phoneNumberInput.setText(userPatient.generalDescription.usrPrimaryPhone)
                        fullLegalNameInput.setText(userPatient.generalDescription.usrFullLegalName)
                        emailAddressInput.setText(userPatient.generalDescription.usrPrimaryEmail)
                        dateOfBirthInput.setText(userPatient.generalDescription.usrDateOfBirth)
                        currentCountryInput.setText(userPatient.generalDescription.countryLocation)

                        profileWeightTextEdit.setText(userPatient.generalHealthInformation.weightRecord)
                        profileAllergiesTextEdit.setText(userPatient.generalHealthInformation.allergiesRecord)
                        profileBloodGroupTextEdit.setText(userPatient.generalHealthInformation.bloodGroupRecord)
                        profileHeightTextEdit.setText(userPatient.generalHealthInformation.heightRecord)

                        Glide.with(requireContext())
                            .load(userPatient.avatar)
                            .placeholder(R.drawable.holder)
                            .into(imgAvatar)
                    }
                }
            }

            closeBottomSheetProfile.setOnClickListener {
                dismiss()
                initProfileDetailsIfNeeded()
                startProfileDetailsListener()
            }
        }
    }

    private fun updateFirebaseDatabase(id: String, value: String) {
        if (value != "null" && value != "Not Set") {
            val uid = auth.uid
            val databaseRef = db.getReference("participants/$uid")
            databaseRef.child(id).setValue(value)
        }
    }

    @Suppress("DEPRECATION")
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
            val imageRef =
                storageRef.child("participants/profile_images/$uid/${"avatar"}.$fileExtension")
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

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude

                println("Location $latitude $longitude")
                val userCountry = getApproximateLocation(requireContext(), latitude, longitude)
                updateFirebaseDatabase("generalDescription/countryLocation", userCountry.toString())
            } else {
                MoluccusToast(requireContext()).showError("Location UnKnown")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}