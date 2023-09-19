package metospherus.app.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.Layout
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.AlignmentSpan
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
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
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.callbacks.onPreShow
import com.afollestad.materialdialogs.customview.customView
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.search.SearchBar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
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
import metospherus.app.database.profile_data.GeneralUserInformation
import metospherus.app.databinding.FragmentHomeBinding
import metospherus.app.modules.FormattingInfo
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
import metospherus.app.utilities.requestFeatureOrReportBugs
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

    private var isBold = false
    private var isItalic = false

    private lateinit var metospherus: MoluccusToast
    private lateinit var documentInputEditText: TextInputEditText

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

    val liveDataInput = MutableLiveData<String>()
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
        categoryAdapter = CategoriesAdaptor(requireContext(), appDatabase, findNavController())

        recyclerViewTracker.adapter = mainAdapter
        metospherus = MoluccusToast(requireContext())

        initializeTracker()
        initializeCatagories()

        binding.fab.setOnClickListener {
            initBottomSheets()
        }

        binding.addRemoveAvailableModules.setOnClickListener {
            if (auth.currentUser?.uid != null) {
                AddOrRemoveModules().addOrRemoveModules(requireContext(), db, auth)
            } else {
                MoluccusToast(requireContext()).showInformation("Please Login In Order To Use This Feature!!")
            }
        }

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
                retrieveRealtimeDatabaseOnListener(db,
                    "participants/${userId}",
                    requireContext(),
                    onDataChange = { snapshot ->
                        val userProfile = snapshot.getValue(GeneralUserInformation::class.java)
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
                retrieveRealtimeDatabase(db, "participants/${userId}").removeEventListener(
                    listener
                )
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

                val searchRecyclerView =
                    view.findViewById<RecyclerView>(R.id.searchResultsAnswer)
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
                            searchRecyclerView.loadSkeleton(R.layout.search_view_layout) {
                                itemCount(3)
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

                                    searchRecyclerView.loadSkeleton(R.layout.search_view_layout) {
                                        itemCount(3)
                                    }
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

            onPreShow {
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
                                    val restartIntent =
                                        Intent(context, MainActivity::class.java)
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

    private fun initProfileSheetIfNeeded(usrPatient: GeneralUserInformation) {
        MaterialDialog(requireContext()).show {
            customView(R.layout.profiletools_layout)
            cornerRadius(20f)
            cancelOnTouchOutside(false)

            val profileDetails = MutableLiveData<GeneralUserInformation>()
            onPreShow {
                val displayMetrics = windowContext.resources.displayMetrics
                val dialogWidth =
                    displayMetrics.widthPixels - (2 * windowContext.resources.getDimensionPixelSize(
                        R.dimen.dialog_margin_horizontal
                    ))
                window?.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)

                profileDetails.postValue(usrPatient)
            }
            val profileActionLayout = view.findViewById<ImageView>(R.id.profileActionLayout)
            val shareInformation = view.findViewById<ImageView>(R.id.shareInformation)
            val closeHolder = view.findViewById<ImageView>(R.id.closeHolder)
            val profileImage = view.findViewById<ImageView>(R.id.profileImage)
            val settingActionLayout = view.findViewById<ImageView>(R.id.settingActionLayout)

            val reportBugsOrProblems =
                view.findViewById<LinearLayout>(R.id.reportBugsOrProblems)

            val prefName = view.findViewById<TextView>(R.id.prefName)
            val profileType = view.findViewById<TextView>(R.id.profileType)
            val profileHandler = view.findViewById<TextView>(R.id.profileHandler)

            profileDetails.observeForever { userPatient ->
                prefName.text = userPatient.generalDescription.usrPreferedName
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

                settingActionLayout.setOnClickListener {
                    dismiss()
                    findNavController().navigate(R.id.action_to_settings)
                }

                closeHolder.setOnClickListener {
                    dismiss()
                }
            }

            reportBugsOrProblems.setOnClickListener {
                requestFeatureOrReportBugs(requireContext())
            }

            onDismiss {
                profileDetails.observeForever {}
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
                    cornerRadius(literalDp = 30f)

                    val spanInfoList = ArrayList<FormattingInfo>()

                    val addOrSaveDocument = view.findViewById<FloatingActionButton>(R.id.addOrSaveDocument)
                    documentInputEditText = view.findViewById(R.id.documentInputEditText)
                    val markSelectedTextBold = findViewById<FloatingActionButton>(R.id.markSelectedTextBold)
                    val markSelectedTextItalic = findViewById<FloatingActionButton>(R.id.markSelectedTextItalic)
                    val addBulletDotsTo = findViewById<FloatingActionButton>(R.id.addBulletDotsTo)
                    val alignSelectedTextToTextStart = findViewById<FloatingActionButton>(R.id.alignSelectedTextToTextStart)
                    val alignSelectedTextToTextCenter = findViewById<FloatingActionButton>(R.id.alignSelectedTextToTextCenter)
                    val alignSelectedTextToTextEnd = findViewById<FloatingActionButton>(R.id.alignSelectedTextToTextEnd)

                    markSelectedTextBold.setOnClickListener {
                        toggleBoldStyle()
                    }

                    markSelectedTextItalic.setOnClickListener {
                        toggleItalicStyle()
                    }

                    addBulletDotsTo.setOnClickListener {
                        val selectionStart = documentInputEditText.selectionStart
                        val selectionEnd = documentInputEditText.selectionEnd
                        val bulletPoint = "\u2022 "
                        val spannable = SpannableStringBuilder(documentInputEditText.text)
                        spannable.insert(selectionStart, bulletPoint)
                        documentInputEditText.text = spannable
                        documentInputEditText.setSelection(selectionStart + bulletPoint.length)
                    }

                    alignSelectedTextToTextStart.setOnClickListener {
                        val selectionStart = documentInputEditText.selectionStart
                        val selectionEnd = documentInputEditText.selectionEnd
                        val alignmentSpan = AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL)
                        val spannable = SpannableStringBuilder(documentInputEditText.text)
                        spannable.setSpan(alignmentSpan, selectionStart, selectionEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                        documentInputEditText.text = spannable
                    }

                    alignSelectedTextToTextCenter.setOnClickListener {
                        val selectionStart = documentInputEditText.selectionStart
                        val selectionEnd = documentInputEditText.selectionEnd
                        val alignmentSpan = AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER)
                        val spannable = SpannableStringBuilder(documentInputEditText.text)
                        spannable.setSpan(alignmentSpan, selectionStart, selectionEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                        documentInputEditText.text = spannable
                    }

                    alignSelectedTextToTextEnd.setOnClickListener {
                        val selectionStart = documentInputEditText.selectionStart
                        val selectionEnd = documentInputEditText.selectionEnd
                        val alignmentSpan = AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE)
                        val spannable = SpannableStringBuilder(documentInputEditText.text)
                        spannable.setSpan(alignmentSpan, selectionStart, selectionEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                        documentInputEditText.text = spannable
                    }

                    documentInputEditText.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                        override fun afterTextChanged(editable: Editable?) {
                            val selectionStart = documentInputEditText.selectionStart
                            val selectionEnd = documentInputEditText.selectionEnd

                            documentInputEditText.setSelection(selectionStart, selectionEnd)
                            liveDataInput.postValue(editable.toString())

                            val spans = documentInputEditText.text?.getSpans(0, documentInputEditText.text!!.length, CharacterStyle::class.java)
                            if (spans != null) {
                                for (span in spans) {
                                    if (span is StyleSpan) {
                                        val style = span.style
                                        val start = documentInputEditText.text?.getSpanStart(span)
                                        val end = documentInputEditText.text?.getSpanEnd(span)
                                        val alignment = getAlignmentForSpan(span)
                                        val formattingInfo = FormattingInfo(start!!, end!!, style, alignment)
                                        spanInfoList.add(formattingInfo)
                                    }
                                }
                            }
                        }
                    })

                    addOrSaveDocument.setOnClickListener {
                        val startTimestamp = System.currentTimeMillis() / 1000
                        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        val date = dateFormat.format(startTimestamp * 1000) // Convert to milliseconds

                        val firebaseData = HashMap<String, Any>()
                        firebaseData["time"] = date.toString()
                        firebaseData["formattedText"] = liveDataInput.value.toString()
                        val spanInfoListJson = Gson().toJson(spanInfoList)
                        firebaseData["formattingInfoList"] = spanInfoListJson
                        if (firebaseData.isNotEmpty()) {
                            db.getReference("MedicalDocuments/${auth.currentUser?.uid}").push()
                                .setValue(firebaseData)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        dismiss()
                                    } else {
                                        documentInputEditText.error = "Error ${it.exception?.message}"
                                    }
                                }
                        }
                    }

                    onPreShow {
                        val displayMetrics = windowContext.resources.displayMetrics
                        val dialogWidth = displayMetrics.widthPixels - (2 * windowContext.resources.getDimensionPixelSize(
                            R.dimen.dialog_margin_horizontal
                        ))

                        // Set the dialog width
                        val layoutParams = window?.attributes
                        layoutParams?.width = dialogWidth

                        // Set the dialog position to 5dp from the bottom
                        layoutParams?.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                        layoutParams?.y = Constructor.dpToPx(10) // Adjust this value as needed

                        window?.attributes = layoutParams
                    }
                }
            }
        }
    }

    private fun getAlignmentForSpan(span: CharacterStyle): Layout.Alignment? {
        if (span is AlignmentSpan) {
            return span.alignment
        }
        return null
    }

    private fun toggleBoldStyle() {
        val selectionStart = documentInputEditText.selectionStart
        val selectionEnd = documentInputEditText.selectionEnd

        val styleSpan = StyleSpan(Typeface.BOLD)
        val styleSpanNormal = StyleSpan(Typeface.NORMAL)
        val spannable = SpannableStringBuilder(documentInputEditText.text)

        if (isTextStyled(spannable, selectionStart, selectionEnd, Typeface.BOLD)) {
            spannable.setSpan(styleSpanNormal, selectionStart, selectionEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            spannable.removeSpan(styleSpan)
            isBold = false
            println("Removed Bold Style")
        } else {
            spannable.setSpan(styleSpan, selectionStart, selectionEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            isBold = true
            println("Applied Bold Style")
        }

        documentInputEditText.text = spannable
        documentInputEditText.setSelection(selectionStart, selectionEnd)
    }

    private fun toggleItalicStyle() {
        val selectionStart = documentInputEditText.selectionStart
        val selectionEnd = documentInputEditText.selectionEnd

        val styleSpan = StyleSpan(Typeface.ITALIC)
        val styleSpanNormal = StyleSpan(Typeface.NORMAL)
        val spannable = SpannableStringBuilder(documentInputEditText.text)

        if (isTextStyled(spannable, selectionStart, selectionEnd, Typeface.ITALIC)) {
            spannable.setSpan(styleSpanNormal, selectionStart, selectionEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            spannable.removeSpan(styleSpan)
            isItalic = false
            println("Removed Italic Style")
        } else {
            spannable.setSpan(styleSpan, selectionStart, selectionEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            isItalic = true
            println("Applied Italic Style")
        }

        documentInputEditText.text = spannable
        documentInputEditText.setSelection(selectionStart, selectionEnd)
    }

    private fun isTextStyled(editable: Editable?, start: Int, end: Int, style: Int): Boolean {
        if (editable == null) return false
        val spans = editable.getSpans(start, end, StyleSpan::class.java)
        for (span in spans) {
            if (span.style == style) {
                return true
            }
        }
        return false
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
