package metospherus.app.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.bottomsheets.setPeekHeight
import com.afollestad.materialdialogs.callbacks.onPreShow
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.search.SearchBar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import metospherus.app.MainActivity
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
import metospherus.app.utilities.Constructor.getCompanionShipFromLocalDatabase
import metospherus.app.utilities.FirebaseConfig.retrieveRealtimeDatabase
import metospherus.app.utilities.FirebaseConfig.retrieveRealtimeDatabaseOnListener
import metospherus.app.utilities.MoluccusToast
import metospherus.app.utilities.Validator.Companion.isValidEmail
import metospherus.app.utilities.initBottomSheetsIfNeeded
import java.text.SimpleDateFormat
import java.util.Locale

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

    private lateinit var metospherus: MoluccusToast

    private var profileDetailsListener: ValueEventListener? = null
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

        mainAdapter = MainAdaptor(requireContext(), lifecycleScope)
        categoryAdapter = CategoriesAdaptor(requireContext(), appDatabase)

        recyclerViewTracker.adapter = mainAdapter
        metospherus = MoluccusToast(requireContext())

        initializeTracker()
        initializeCatagories()

        binding.fab.setOnClickListener {
            initBottomSheets()
        }

        binding.addRemoveAvailableModules.setOnClickListener {
            AddOrRemoveModules().addOrRemoveModules(requireContext(), db, auth)
        }

        var isDataLoading = false
        CoroutineScope(Dispatchers.Main).launch {
            val userPatient = Constructor.getUserProfilesFromDatabase(appDatabase)
            userPatient?.let { profile ->
                Glide.with(requireContext())
                    .load(profile.avatar)
                    .placeholder(R.drawable.holder)
                    .into(binding.profilePicture)

                binding.profileHolder.setOnClickListener {
                    when {
                        auth.currentUser != null -> {
                            initProfileSheetIfNeeded(profile)
                        }
                        else -> {
                            initBottomSheetsIfNeeded(requireContext(), requireActivity(), auth, db)
                        }
                    }
                }
            } ?: run {
                getPermissionsTaskDB()
            }
        }
        setupMaterialSearchView()
        getPermissionsTaskDB()
        startProfileDetailsListener()
    }

    override fun onResume() {
        super.onResume()
        startProfileDetailsListener()
    }

    override fun onPause() {
        super.onPause()
        stopProfileDetailsListener()
    }

    private fun startProfileDetailsListener() {
        if (profileDetailsListener == null) {
            auth.currentUser?.uid?.let { userId ->
                retrieveRealtimeDatabaseOnListener(db, "participants/${userId}", requireContext(),
                    onDataChange = { snapshot ->
                        val userProfile = snapshot.getValue(Profiles::class.java)
                        if (userProfile != null) {
                            CoroutineScope(Dispatchers.Default).launch {
                                Constructor.insertOrUpdateUserProfile(userProfile, appDatabase)
                            }
                        }
                    })
            }
        }
    }

    private fun stopProfileDetailsListener() {
        profileDetailsListener?.let { listener ->
            auth.currentUser?.uid?.let { userId ->
                retrieveRealtimeDatabase(db, "participants/${userId}").removeEventListener(listener)
            }
            profileDetailsListener = null
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
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
                val searchHistory = mutableListOf<GeneralSearchResults>()
                val searchPreviewResults = listOf(
                    GeneralSearchResults(
                        "Metospherus Comprehensive Medical System",
                        "Welcome and thanks for trying out Trevus our system brain",
                    ),
                    GeneralSearchResults(
                        "Metospherus Comprehensive Medical System",
                        "Would you like me to help you with any inquiries"
                    )
                )
                searchHistory.addAll(searchPreviewResults)
                GlobalScope.launch(Dispatchers.IO) {
                    val generalSearchResults = getCompanionShipFromLocalDatabase(appDatabase)
                    if (generalSearchResults != null) {
                        val companionship = mutableListOf(
                            GeneralSearchResults(
                                generalSearchResults.questionAsked,
                                generalSearchResults.responsesGiven
                            )
                        )
                        searchHistory.addAll(companionship)
                        withContext(Dispatchers.Main) {
                            searchAdapter.setData(searchHistory)
                        }
                    }
                    searchAdapter.setData(searchHistory)
                }
                searchableInputEditText.doAfterTextChanged {
                    val query = searchableInputEditText.text.toString()
                    val matchingResults = searchHistory.filter { result ->
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
                                    sendMessage(
                                        context,
                                        query,
                                        searchAdapter,
                                        searchRecyclerView,
                                        appDatabase
                                    )
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
        auth.currentUser?.uid?.let { userId ->
            retrieveRealtimeDatabaseOnListener(db, "participants/${userId}", requireContext(),
                onDataChange = { snapshot ->
                    if (!snapshot.child("generalDatabaseInformation")
                            .hasChild("userGeneralIdentificationNumber")
                    ) {
                        initCompleteUserProfile(snapshot.ref)
                    }
                })
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
                    usersEmailLayout.error = null
                } else {
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
                            "generalDatabaseInformation" to mapOf("userGeneralIdentificationNumber" to userIdentification),
                            "generalDescription" to mapOf(
                                "usrPreferedName" to userPreferedName.text.toString(),
                                "usrPrimaryEmail" to email,
                                "usrDistinguishedHandle" to "@$handle"
                            )
                        )

                        usrDb.updateChildren(addProfileDetails)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    dismiss()
                                    val restartIntent = Intent(context, MainActivity::class.java)
                                    restartIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    context.startActivity(restartIntent)
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

    private fun initializeCatagories() {
        val trackerInstance = mutableListOf<GeneralCategory>()
        retrieveRealtimeDatabaseOnListener(db,
            "medicalmodules/Categories",
            context = requireContext(),
            onDataChange = { snapshot ->
                trackerInstance.clear()
                for (dataSnapshot in snapshot.children) {
                    val modulesData = dataSnapshot.getValue(GeneralCategory::class.java)
                    if (modulesData?.enabled == true) {
                        trackerInstance.add(modulesData)
                    }
                }
                categoryAdapter.setData(trackerInstance)
                recylerCatagories.adapter = categoryAdapter
            })
    }

    private fun initializeTracker() {
        auth.currentUser?.let { currentUser ->
            val moduleTemps = mutableListOf<GeneralTemplate>()
            retrieveRealtimeDatabaseOnListener(db,
                "medicalmodules/userspecific/modules/${currentUser.uid}",
                requireContext(),
                onDataChange = { snapshot ->
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

            val profileActionLayout = view.findViewById<ImageView>(R.id.profileActionLayout)
            val shareInformation = view.findViewById<ImageView>(R.id.shareInformation)
            val closeHolder = view.findViewById<ImageView>(R.id.closeHolder)
            val profileImage = view.findViewById<ImageView>(R.id.profileImage)
            val settingActionLayout = view.findViewById<ImageView>(R.id.settingActionLayout)

            val prefName = view.findViewById<TextView>(R.id.prefName)
            val profileType = view.findViewById<TextView>(R.id.profileType)
            val profileHandler = view.findViewById<TextView>(R.id.profileHandler)

            onPreShow {
                prefName.text = userPatient.generalDescription.usrPreferedName
                profileType.text = userPatient.accountType
                profileHandler.text = userPatient.handle
            }
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

            settingActionLayout.setOnClickListener {
                dismiss()
                findNavController().navigate(R.id.action_to_settings)
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
