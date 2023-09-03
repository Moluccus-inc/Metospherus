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
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.bottomsheets.setPeekHeight
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.search.SearchBar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.hbb20.countrypicker.config.CPDialogViewIds
import com.hbb20.countrypicker.dialog.launchCountryPickerDialog
import com.hbb20.countrypicker.models.CPCountry
import `in`.aabhasjindal.otptextview.OTPListener
import `in`.aabhasjindal.otptextview.OtpTextView
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import metospherus.app.R
import metospherus.app.adaptors.CategoriesAdaptor
import metospherus.app.adaptors.MainAdaptor
import metospherus.app.adaptors.SearchAdaptor
import metospherus.app.database.localhost.AppDatabase
import metospherus.app.database.profile_data.Profiles
import metospherus.app.databinding.FragmentHomeBinding
import metospherus.app.modules.GeneralBrain.sendMessage
import metospherus.app.modules.GeneralCategory
import metospherus.app.modules.GeneralSearchResults
import metospherus.app.modules.GeneralTemplate
import metospherus.app.utilities.AddOrRemoveModules
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
    private lateinit var searchAdapter: SearchAdaptor

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var appDatabase: AppDatabase
    private lateinit var imageHolder: ImageView
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var storedVerificationId: String

    private val selectedImageLiveData = MutableLiveData<Uri>()
    private val imguriholder = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uris ->
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

        mainAdapter = MainAdaptor(requireContext(), lifecycleScope)
        categoryAdapter = CategoriesAdaptor(requireContext(), appDatabase)

        initializeTracker()
        initializeCatagories()

        binding.fab.setOnClickListener {
            initBottomSheets()
        }

        binding.addRemoveAvailableModules.setOnClickListener {
            AddOrRemoveModules().addOrRemoveModules(requireContext(), db, auth)
        }

        var isDataLoading = false
        binding.profileHolder.setOnClickListener {
            if (isDataLoading) {
                return@setOnClickListener
            }
            if (auth.currentUser != null) {
                isDataLoading = true
                CoroutineScope(Dispatchers.Main).launch {
                    val userPatient = Constructor.getUserProfilesFromDatabase(appDatabase)
                    if (userPatient != null) {
                        initProfileSheetIfNeeded(userPatient)
                    } else {
                        getPermissionsTaskDB()
                    }
                    isDataLoading = false
                }
            } else {
                initBottomSheetsIfNeeded()
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            val userPatient = Constructor.getUserProfilesFromDatabase(appDatabase)
            if (userPatient != null) {
                Glide.with(requireContext())
                    .load(userPatient.avatar)
                    .placeholder(R.drawable.holder)
                    .into(binding.profilePicture)
            }
        }
        setupMaterialSearchView()
        getPermissionsTaskDB()
    }

    private fun setupMaterialSearchView() {
        val searchBar = view?.findViewById<SearchBar>(R.id.search_bar)
        searchBar?.setOnClickListener {
            MaterialDialog(requireContext(), BottomSheet(LayoutMode.MATCH_PARENT)).show {
                customView(R.layout.search_results_answer_layout)
                cornerRadius(literalDp = 20f)
                setPeekHeight(Int.MAX_VALUE)

                val searchRecyclerView = view.findViewById<RecyclerView>(R.id.searchResultsAnswer)
                val searchableInputEditText =
                    view.findViewById<TextInputEditText>(R.id.searchableInputEditText)

                searchAdapter = SearchAdaptor(requireContext())
                searchRecyclerView?.layoutManager = LinearLayoutManager(requireContext())
                searchRecyclerView?.adapter = searchAdapter
                val searchPreviewResults = mutableListOf(
                    GeneralSearchResults(
                        "Metospherus Comprehensive Medical System",
                        "Welcome and thanks for trying out Trevus our system brain",
                    ),
                    GeneralSearchResults(
                        "Metospherus Comprehensive Medical System",
                        "Would you like me to help you with any inquiries"
                    )
                )
                searchAdapter.setData(searchPreviewResults)
                searchableInputEditText.doAfterTextChanged {
                    val query = searchableInputEditText.text.toString()
                    val matchingResults = searchPreviewResults.filter { result ->
                        result.searchResponse.contains(query, ignoreCase = true)
                    }
                    when {
                        matchingResults.isNullOrEmpty() -> {
                            val repos = mutableListOf(
                                GeneralSearchResults(
                                    "...",
                                    "...",
                                ),
                                GeneralSearchResults(
                                    "...",
                                    "...",
                                ),
                                GeneralSearchResults(
                                    "...",
                                    "...",
                                ),
                                GeneralSearchResults(
                                    "...",
                                    "...",
                                ),
                                GeneralSearchResults(
                                    "...",
                                    "...",
                                ),
                            )
                            searchAdapter.setData(repos)

                            searchRecyclerView.loadSkeleton {
                                val customShimmer = Shimmer.AlphaHighlightBuilder()
                                    .setDirection(Shimmer.Direction.TOP_TO_BOTTOM)
                                    .build()
                                shimmer(customShimmer)
                            }

                            searchableInputEditText.setOnEditorActionListener { _, actionId, _ ->
                                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                                    sendMessage(context, query, searchAdapter, searchRecyclerView)
                                }
                                true
                            }
                        }

                        else -> {
                            searchRecyclerView.hideSkeleton()
                            searchAdapter.setData(matchingResults.toMutableList())
                        }
                    }
                }
            }
        }

    }

    private fun getPermissionsTaskDB() {
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
                                val startTimestamp =
                                    System.currentTimeMillis() / 1000 // Get current timestamp in seconds
                                val dateFormat =
                                    SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault())
                                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

                                val exitDate =
                                    dateFormat.format(startTimestamp * 1000) // Convert to milliseconds
                                val exitTime =
                                    timeFormat.format(startTimestamp * 1000) // Convert to milliseconds

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
    }

    private fun initCompleteUserProfile(usrDb: DatabaseReference) {
        MaterialDialog(requireContext()).show {
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
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                accountTypes
            )
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


            userHandle.doAfterTextChanged { editable ->
                val input = editable.toString()

                val validInput = input.replace(" ", "_") // Replace spaces with underscores
                    .replace(".", "_") // Replace dots with underscores
                    .replace(Regex("[^a-zA-Z0-9_]"), "")
                    .lowercase(Locale.ROOT)// Remove all characters except letters, digits, and underscore

                // Update the EditText with the valid input
                if (input != validInput) {
                    editable?.replace(0, editable.length, validInput)
                }
            }

            usersEmail.doAfterTextChanged {
                val input = it.toString()
                if (isValidEmail(input)) {
                    // Email is valid, remove any previous error message if shown
                    usersEmailLayout.error = null
                } else {
                    // Show an error message to the user indicating that the email format is incorrect
                    usersEmailLayout.error = "Invalid email format"
                }
            }

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
                        val uidDigits =
                            uidSubstring?.hashCode()?.toString()?.replace("-", "")?.take(12)
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
                                    MoluccusToast(requireContext()).showSuccess("Profile updated successfully!!\uD83D\uDE0A")
                                } else {
                                    MoluccusToast(requireContext()).showError("Error: ${task.exception?.message}")
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
        recyclerViewTracker.adapter = mainAdapter
        auth.currentUser?.let { currentUser ->
            val patientGeneralModulesDB = db.getReference("medicalmodules")
                .child("userspecific")
                .child("modules").child(currentUser.uid)
            patientGeneralModulesDB.keepSynced(true)
            patientGeneralModulesDB.addValueEventListener(object : ValueEventListener {
                val moduleTemps = mutableListOf<GeneralTemplate>()
                override fun onDataChange(snapshot: DataSnapshot) {
                    moduleTemps.clear()
                    for (snapshotItem in snapshot.children) {
                        val modulesData = snapshotItem.getValue(GeneralTemplate::class.java)
                        if (modulesData != null) {
                            if (modulesData.selected == true) {
                                moduleTemps.add(modulesData)
                            }
                        }
                    }
                    mainAdapter.setData(moduleTemps)
                }

                override fun onCancelled(error: DatabaseError) {
                    MoluccusToast(requireContext()).showError("Cancelled ${error.message}")
                }
            })
        }

        val notLoggedIn = mutableListOf(
            GeneralTemplate(
                "General Health",
                getString(R.string.app_default_module_url),
                true,
            )
        )
        mainAdapter.setData(notLoggedIn)
    }

    private fun initProfileSheetIfNeeded(userPatient: Profiles) {
        MaterialDialog(requireContext()).show {
            customView(R.layout.profiletools_layout)
            cornerRadius(20f)
            cancelOnTouchOutside(false)

            val profileActionLayout = view.findViewById<MaterialCardView>(R.id.profileActionLayout)
            val shareInformation = view.findViewById<MaterialCardView>(R.id.shareInformation)
            val closeHolder = view.findViewById<ImageView>(R.id.closeHolder)
            val profileImage = view.findViewById<ImageView>(R.id.profileImage)

            val prefName = view.findViewById<TextView>(R.id.prefName)
            val profileType = view.findViewById<TextView>(R.id.profileType)
            val profileHandler = view.findViewById<TextView>(R.id.profileHandler)

            prefName.text = userPatient.name
            profileType.text = userPatient.accountType
            profileHandler.text = userPatient.handle

            Glide.with(requireContext())
                .load(userPatient.avatar)
                .placeholder(R.drawable.holder)
                .into(profileImage)

            profileActionLayout.setOnClickListener {
                when (userPatient.accountType?.lowercase(Locale.ROOT)) {
                    "patient" -> {
                        findNavController().navigate(R.id.action_to_profile)
                        dismiss()
                    }

                    "doctor", "nurse" -> {
                        findNavController().navigate(R.id.action_medical_profile)
                        dismiss()
                    }

                    else -> {
                        MoluccusToast(context).showInformation("Your Account Type is under development!!")
                    }
                }
            }

            shareInformation.setOnClickListener {

            }

            closeHolder.setOnClickListener {
                dismiss()
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

    @SuppressLint("SetTextI18n")
    private fun initBottomSheetsIfNeeded() {
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            customView(R.layout.authentication_layout)
            cornerRadius(20f)
            cancelOnTouchOutside(false)
            setPeekHeight(Int.MAX_VALUE)

            val imgFlagHolder = view.findViewById<TextView>(R.id.imgFlagHolder)
            val countryName = view.findViewById<TextView>(R.id.countryName)
            val countryCodeName = view.findViewById<TextView>(R.id.countryCodeName)

            val phoneInputLayout = view.findViewById<LinearLayoutCompat>(R.id.phoneInputLayout)
            val codeInputLayout = view.findViewById<LinearLayoutCompat>(R.id.codeInputLayout)
            val otpView = view.findViewById<OtpTextView>(R.id.otp_view)
            val enterOTP = view.findViewById<TextView>(R.id.enterOTP)
            val createAccountConsent =
                view.findViewById<MaterialCheckBox>(R.id.createAccountConsent)
            val resendTokeOpt = view.findViewById<TextView>(R.id.resendTokeOpt)

            val phoneNumberButton = view.findViewById<MaterialButton>(R.id.phoneNumberButton)
            val completeVerification = view.findViewById<MaterialButton>(R.id.completeVerification)
            val phoneNumberInputs = view.findViewById<EditText>(R.id.phoneNumberInput)
            val countryCodePickerLayout =
                view.findViewById<MaterialCardView>(R.id.countryCodePickerLayout)

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
                    val medicalNotes = view.findViewById<TextInputEditText>(R.id.medicalNotes)

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
                        if (documentTitle.text!!.isNotEmpty() || shortDescription.text!!.isNotEmpty() || medicalFacilityName.text!!.isNotEmpty() || medicalNotes.text!!.isNotEmpty()) {
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
                                                    "documentOwnId" to auth.currentUser!!.uid,
                                                    "documentTitle" to documentTitle.text!!.trim()
                                                        .toString(),
                                                    "documentShortDescription" to shortDescription.text!!.trim()
                                                        .toString(),
                                                    "documentDate" to date.toString(),
                                                    "documentSyncStatus" to "Synced",
                                                    "documentNotes" to medicalNotes.text!!.trim()
                                                        .toString().replace(".", ".\n\n")
                                                        .replace("?", "?\n\n")
                                                        .replace("\n\n ", "\n\n"),
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
