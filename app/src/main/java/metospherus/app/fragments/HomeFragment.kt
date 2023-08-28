package metospherus.app.fragments

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.hbb20.countrypicker.config.CPDialogViewIds
import com.hbb20.countrypicker.dialog.launchCountryPickerDialog
import com.hbb20.countrypicker.models.CPCountry
import `in`.aabhasjindal.otptextview.OTPListener
import `in`.aabhasjindal.otptextview.OtpTextView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import metospherus.app.R
import metospherus.app.adaptors.CategoriesAdaptor
import metospherus.app.adaptors.MainAdaptor
import metospherus.app.database.localhost.AppDatabase
import metospherus.app.databinding.FragmentHomeBinding
import metospherus.app.modules.GeneralCategory
import metospherus.app.modules.GeneralTemplate
import metospherus.app.utilities.Constructor
import metospherus.app.utilities.Constructor.hide
import metospherus.app.utilities.Constructor.show
import metospherus.app.utilities.MoluccusToast
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerViewTracker: RecyclerView
    private lateinit var recylerCatagories: RecyclerView

    private lateinit var mainAdapter: MainAdaptor
    private lateinit var categoryAdapter: CategoriesAdaptor

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var appDatabase: AppDatabase
    private lateinit var imageHolder: ImageView
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var storedVerificationId: String

    private val selectedImageLiveData = MutableLiveData<Uri>()
    private val imguriholder =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uris ->
            if (uris != null) {
                selectedImageLiveData.value = uris
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        auth.useAppLanguage()
        db = FirebaseDatabase.getInstance()
        appDatabase = AppDatabase.getInstance(requireContext())

        // recyclerViews
        recylerCatagories = binding.recylerCatagories
        recyclerViewTracker = binding.recyclerViewTrackers

        recylerCatagories.layoutManager = GridLayoutManager(requireContext(), 4)
        recyclerViewTracker.layoutManager = GridLayoutManager(requireContext(), 3)

        mainAdapter = MainAdaptor(requireContext())
        categoryAdapter = CategoriesAdaptor(requireContext())

        initializeTracker()
        initializeCatagories()

        binding.fab.setOnClickListener {
            initBottomSheets()
        }

        binding.profileHolder.setOnClickListener {
            if (auth.currentUser != null) {
                initProfileSheetIfNeeded()
            } else {
                initBottomSheetsIfNeeded()
            }
        }

        lifecycleScope.launch {
            val userPatient = Constructor.getUserProfilesFromDatabase(appDatabase)
            if (userPatient != null) {
                Glide.with(requireContext())
                    .load(userPatient.avatar)
                    .placeholder(R.drawable.holder)
                    .into(binding.profilePicture)
            }
        }

    }
    private fun initializeCatagories() {
        val categoriesGeneralModulesDB = db.getReference("medicalmodules").child("Categories")
        categoriesGeneralModulesDB.keepSynced(true)
        categoriesGeneralModulesDB.addValueEventListener(object : ValueEventListener {
            val trackerInstance = mutableListOf<GeneralCategory>()
            override fun onDataChange(snapshot: DataSnapshot) {
                trackerInstance.clear()
                for (dataSnapshot in snapshot.children) {
                    val modulesData = dataSnapshot.getValue(GeneralCategory::class.java)
                    if (modulesData?.enabled == true) {
                        trackerInstance.add(modulesData)
                    }
                }
                categoryAdapter.setData(trackerInstance)
                recylerCatagories.adapter = categoryAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                MoluccusToast(requireContext()).showError("Cancelled ${error.message}")
            }
        })
    }

    private fun initializeTracker() {
        val patientGeneralModulesDB = db.getReference("medicalmodules").child("modules")
        patientGeneralModulesDB.keepSynced(true)
        patientGeneralModulesDB.addValueEventListener(object : ValueEventListener {
            val moduleTemps = mutableListOf<GeneralTemplate>()
            override fun onDataChange(snapshot: DataSnapshot) {
                moduleTemps.clear()
                for (dataSnapshot in snapshot.children) {
                    val modulesData = dataSnapshot.getValue(GeneralTemplate::class.java)
                    if (modulesData?.selected == true) {
                        moduleTemps.add(modulesData)
                    }
                }
                mainAdapter.setData(moduleTemps)
                recyclerViewTracker.adapter = mainAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                MoluccusToast(requireContext()).showError("Cancelled ${error.message}")
            }
        })
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun initProfileSheetIfNeeded() {
        GlobalScope.launch(Dispatchers.Main) {
            MaterialDialog(requireContext()).show {
                customView(R.layout.profiletools_layout)
                cornerRadius(20f)
                cancelOnTouchOutside(false)

                val profileActionLayout =
                    view.findViewById<MaterialCardView>(R.id.profileActionLayout)
                val closeHolder = view.findViewById<ImageView>(R.id.closeHolder)
                val profileImage = view.findViewById<ImageView>(R.id.profileImage)

                val prefName = view.findViewById<TextView>(R.id.prefName)
                val profileType = view.findViewById<TextView>(R.id.profileType)
                val profileHandler = view.findViewById<TextView>(R.id.profileHandler)

                profileActionLayout.setOnClickListener {
                    findNavController().navigate(R.id.action_to_profile)
                    dismiss()
                }

                closeHolder.setOnClickListener {
                    dismiss()
                }

                auth.currentUser?.uid?.let { userId ->
                    val profileDetails = db.getReference("participants").child(userId)
                    profileDetails.keepSynced(true)
                    profileDetails.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {

                            prefName.text =
                                snapshot.child("name").getValue(String::class.java) ?: "Unknown"
                            profileHandler.text =
                                snapshot.child("handle").getValue(String::class.java) ?: "Unknown"
                            profileType.text =
                                snapshot.child("accountType").getValue(String::class.java)
                                    ?: "Unknown Type"

                            Glide.with(requireContext())
                                .load(snapshot.child("avatar").getValue(String::class.java))
                                .centerCrop()
                                .placeholder(R.drawable.holder)
                                .into(profileImage)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            MoluccusToast(requireContext()).showError("Cancelled because ${error.message}")
                        }
                    })
                }

                onShow {
                    val displayMetrics = windowContext.resources.displayMetrics
                    val dialogWidth =
                        displayMetrics.widthPixels - (2 * windowContext.resources.getDimensionPixelSize(
                            R.dimen.dialog_margin_horizontal
                        ))
                    window?.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initBottomSheetsIfNeeded() {
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            customView(R.layout.authentication_layout)
            cornerRadius(20f)
            cancelOnTouchOutside(false)

            val imgFlagHolder = view.findViewById<TextView>(R.id.imgFlagHolder)
            val countryName = view.findViewById<TextView>(R.id.countryName)
            val countryCodeName = view.findViewById<TextView>(R.id.countryCodeName)

            val phoneInputLayout = view.findViewById<LinearLayoutCompat>(R.id.phoneInputLayout)
            val codeInputLayout = view.findViewById<LinearLayoutCompat>(R.id.codeInputLayout)
            val otpView = view.findViewById<OtpTextView>(R.id.otp_view)
            val enterOTP = view.findViewById<TextView>(R.id.enterOTP)
            val createAccountConsent = view.findViewById<MaterialCheckBox>(R.id.createAccountConsent)
            val resendTokeOpt = view.findViewById<TextView>(R.id.resendTokeOpt)

            val phoneNumberButton = view.findViewById<MaterialButton>(R.id.phoneNumberButton)
            val completeVerification = view.findViewById<MaterialButton>(R.id.completeVerification)
            val phoneNumberInputs = view.findViewById<EditText>(R.id.phoneNumberInput)
            val countryCodePickerLayout = view.findViewById<MaterialCardView>(R.id.countryCodePickerLayout)

            phoneNumberButton.hide()
            createAccountConsent.isChecked = false
            createAccountConsent.setOnCheckedChangeListener { _, isChecked ->
                when (isChecked) {
                    true -> {
                        phoneNumberButton.show()
                    }
                    false -> {
                        phoneNumberButton.hide()
                    }
                }
            }

            phoneInputLayout.show()
            codeInputLayout.hide()

            val cpDialogViewIds = CPDialogViewIds(
                R.layout.custom_country_code_dialog,
                R.id.customDialogPicker,
                R.id.recyclerViewDialog,
                R.id.title_textview,
                R.id.search_view,
                null,
                null,
            )
            val initialPhCode = "+256"
            phoneNumberInputs.setText(initialPhCode)
            phoneNumberInputs.setSelection(initialPhCode.length)

            countryCodePickerLayout.setOnClickListener {
                windowContext.launchCountryPickerDialog(
                    customMasterCountries = "UG,TZ,KE,TZ,RW,US,CA,DE",
                    preferredCountryCodes = "UG,TZ,KE",
                    dialogViewIds = cpDialogViewIds,
                    secondaryTextGenerator = { country -> country.capitalEnglishName },
                    showFullScreen = true,
                    useCache = false
                ) { selectedCountry: CPCountry? ->
                    imgFlagHolder.text = "${selectedCountry?.flagEmoji}"
                    countryName.text = "${selectedCountry?.name}"
                    countryCodeName.text = "+${selectedCountry?.phoneCode}"
                    phoneNumberInputs.inputType = InputType.TYPE_CLASS_PHONE
                    val initialPhoneCode = "+${selectedCountry?.phoneCode}"
                    phoneNumberInputs.setText(initialPhoneCode)
                    phoneNumberInputs.setSelection(initialPhoneCode.length)
                    phoneNumberInputs.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                        }

                        override fun afterTextChanged(editable: Editable?) {
                            val inputText = editable.toString()
                            val countryCode = "+${selectedCountry?.phoneCode}"

                            if (inputText.startsWith(countryCode)) {
                                // The country code is intact, do nothing
                            } else {
                                phoneNumberInputs.setText(countryCode)
                                phoneNumberInputs.setSelection(countryCode.length)
                            }
                        }
                    })

                }
            }

            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    dismiss()
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    when (e) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            phoneInputLayout.show()
                            codeInputLayout.hide()
                            MoluccusToast(windowContext).showError("Invalid request")
                        }

                        is FirebaseTooManyRequestsException -> {
                            phoneInputLayout.show()
                            codeInputLayout.hide()
                            MoluccusToast(windowContext).showInformation("The SMS quota has been exceeded")
                        }

                        is FirebaseAuthMissingActivityForRecaptchaException -> {
                            phoneInputLayout.show()
                            codeInputLayout.hide()
                            MoluccusToast(windowContext).showInformation("reCAPTCHA verification attempted with null Activity")
                        }
                    }
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken,
                ) {
                    resendToken = token
                    storedVerificationId = verificationId

                    resendTokeOpt.setOnClickListener {
                        enterOTP.clearFocus()
                        phoneInputLayout.show()
                        codeInputLayout.hide()
                    }
                    enterOTP.text = "Enter the OTP code sent to ${
                        phoneNumberInputs.text?.trim().toString()
                            .replace(" ", "")
                    } below to verify your phone number."

                    phoneInputLayout.hide()
                    codeInputLayout.show()

                    otpView.requestFocusOTP()
                    completeVerification.hide()
                    otpView.otpListener = object : OTPListener {
                        override fun onInteractionListener() {}

                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onOTPComplete(otp: String) {
                            completeVerification.show()
                            completeVerification.setOnClickListener {
                                when {
                                    otp.isNotEmpty() -> {
                                        val credentialAuth: PhoneAuthCredential =
                                            PhoneAuthProvider.getCredential(
                                                verificationId,
                                                otp
                                            )

                                        auth.signInWithCredential(credentialAuth)
                                            .addOnCompleteListener(requireActivity()) { task ->
                                                if (task.isSuccessful) {
                                                    val user = task.result?.user
                                                    val createUserDatabase = mapOf(
                                                        "name" to "New Meto",
                                                        "uid" to user?.uid!!,
                                                        "phoneNumber" to user.phoneNumber!!
                                                    )

                                                    auth.currentUser?.let {
                                                        val participants =
                                                            db.getReference("participants")
                                                                .child(it.uid)
                                                        participants.addValueEventListener(object :
                                                            ValueEventListener {
                                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                                if (snapshot.exists()) {
                                                                    dismiss()
                                                                    MoluccusToast(windowContext).showSuccess(
                                                                        "Welcome Back To Metospherus A Comprehensive Medical System."
                                                                    )
                                                                } else {
                                                                    participants.setValue(
                                                                        createUserDatabase
                                                                    )
                                                                        .addOnSuccessListener {
                                                                            dismiss()
                                                                            MoluccusToast(
                                                                                windowContext
                                                                            ).showSuccess("Welcome To Metospherus A Comprehensive Medical System")
                                                                        }
                                                                        .addOnFailureListener { exception ->
                                                                            MoluccusToast(
                                                                                windowContext
                                                                            ).showError("Error !${exception.message}")
                                                                        }
                                                                }
                                                            }

                                                            override fun onCancelled(error: DatabaseError) {
                                                                TODO("Not yet implemented")
                                                            }

                                                        })
                                                    }
                                                } else {
                                                    //Log.w(TAG, "signInWithCredential:failure", task.exception)
                                                    when (task.exception) {
                                                        is FirebaseAuthInvalidCredentialsException -> {
                                                            // The verification code entered was invalid
                                                        }
                                                    }
                                                }
                                            }
                                    }

                                    else -> {
                                        otpView.showError()
                                        otpView.resetState()
                                    }
                                }
                            }
                        }
                    }
                }
            }

            phoneNumberButton.setOnClickListener {
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(
                        phoneNumberInputs.text?.trim().toString().replace(" ", "")
                    )
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(requireActivity())
                    .setCallbacks(callbacks)
                    .build()

                Toast.makeText(
                    requireContext(),
                    phoneNumberInputs.text?.trim().toString().replace(" ", ""),
                    Toast.LENGTH_SHORT
                ).show()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        }
    }

    @SuppressLint("Recycle", "Range")
    private fun initBottomSheets() {
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            customView(R.layout.material_bottomsheet_documents)
            cornerRadius(literalDp = 20f)

            val addDocumentHolder = view.findViewById<FloatingActionButton>(R.id.addDocumentHolder)
            addDocumentHolder.setOnClickListener {
                MaterialDialog(requireContext()).show {
                    customView(R.layout.add_document_medical_record)
                    cornerRadius(literalDp = 20f)

                    val documentTitle = view.findViewById<TextInputEditText>(R.id.documentTitle)
                    val shortDescription =
                        view.findViewById<TextInputEditText>(R.id.shortDescription)
                    val medicalFacilityName =
                        view.findViewById<TextInputEditText>(R.id.medicalFacilityName)
                    val uploadFileOrImage = view.findViewById<ImageView>(R.id.uploadFileOrImage)
                    imageHolder = view.findViewById(R.id.imageHolder)

                    uploadFileOrImage.setOnClickListener {
                        imguriholder.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }

                    selectedImageLiveData.observe(viewLifecycleOwner) { selectedImageUri ->
                        if (selectedImageUri != null) {
                            Glide.with(requireContext())
                                .load(selectedImageUri)
                                .centerCrop()
                                .into(imageHolder)
                        }
                    }

                    onShow {
                        val displayMetrics = windowContext.resources.displayMetrics
                        val dialogWidth =
                            displayMetrics.widthPixels - (2 * windowContext.resources.getDimensionPixelSize(
                                R.dimen.dialog_margin_horizontal
                            ))
                        window?.setLayout(
                            dialogWidth,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    }

                    positiveButton(text = "Add Medical Record") {
                        if (documentTitle.text!!.isNotEmpty() || shortDescription.text!!.isNotEmpty() || medicalFacilityName.text!!.isNotEmpty()) {
                            selectedImageLiveData.observe(viewLifecycleOwner) { selectedImageUri ->
                                if (selectedImageUri != null) {
                                    val uid = auth.currentUser?.uid
                                    if (uid != null) {
                                        val storageRef = FirebaseStorage.getInstance().reference

                                        var fileName: String? = null
                                        when {
                                            selectedImageUri.scheme.equals("content") -> {
                                                val cursor: Cursor? =
                                                    requireContext().contentResolver.query(
                                                        selectedImageUri,
                                                        null,
                                                        null,
                                                        null,
                                                        null
                                                    )
                                                try {
                                                    if (cursor != null && cursor.moveToFirst()) {
                                                        fileName = cursor.getString(
                                                            cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                                                        )
                                                    }
                                                } finally {
                                                    cursor!!.close()
                                                }
                                            }
                                        }
                                        when (fileName) {
                                            null -> {
                                                fileName = selectedImageUri.path
                                                val mark = fileName?.lastIndexOf("/")
                                                if (mark != -1) {
                                                    if (mark != null) {
                                                        fileName = fileName?.substring(mark + 1)
                                                    }
                                                }
                                            }
                                        }
                                        val extension =
                                            fileName?.substring(fileName.lastIndexOf(".") + 1)

                                        val imageRef =
                                            storageRef.child("MedicalDocuments/$uid/${documentTitle.text!!.trim()}.$extension")
                                        val uploadTask = imageRef.putFile(selectedImageUri)
                                        uploadTask.addOnSuccessListener {
                                            imageRef.downloadUrl.addOnSuccessListener { uri ->

                                                val startTimestamp =
                                                    System.currentTimeMillis() / 1000 // Get current timestamp in seconds
                                                val dateFormat = SimpleDateFormat(
                                                    "MMM dd, yyyy",
                                                    Locale.getDefault()
                                                )

                                                val date =
                                                    dateFormat.format(startTimestamp * 1000) // Convert to milliseconds

                                                val medicalRecord = mapOf(
                                                    "documentTitle" to documentTitle.text!!.trim()
                                                        .toString(),
                                                    "documentShortDescription" to shortDescription.text!!.trim()
                                                        .toString(),
                                                    "documentDate" to date.toString(),
                                                    "documentSyncStatus" to "Synced",
                                                    "documentPreview" to uri.toString()
                                                )
                                                db.getReference("MedicalDocuments")
                                                    .child(auth.currentUser!!.uid)
                                                    .push()
                                                    .setValue(medicalRecord)
                                                    .addOnCanceledListener {
                                                        dismiss()
                                                        MoluccusToast(requireContext()).showInformation(
                                                            "SuccuessFully Added To Cloud"
                                                        )
                                                    }
                                            }.addOnFailureListener { exception ->
                                                Log.e(
                                                    "Download URL Error",
                                                    exception.message.toString()
                                                )
                                                MoluccusToast(requireContext()).showInformation("Download URL Error ${exception.message.toString()}")
                                            }
                                        }.addOnFailureListener { exception ->
                                            Log.e("Upload Error", exception.message.toString())
                                            MoluccusToast(requireContext()).showInformation("Upload Error ${exception.message.toString()}")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
