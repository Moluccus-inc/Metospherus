package metospherus.app.utilities

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.NavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.callbacks.onPreShow
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.facebook.shimmer.Shimmer
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton
import metospherus.app.R
import metospherus.app.adaptors.MedicalDocumentsAdaptor
import metospherus.app.adaptors.MedicalProfessionsAdaptor
import metospherus.app.categories.GeneralStatistics
import metospherus.app.categories.PharmacyCategory
import metospherus.app.database.profile_data.Profiles
import metospherus.app.modules.GeneralCategory
import metospherus.app.modules.GeneralDocuments
import metospherus.app.utilities.Constructor.show
import metospherus.app.utilities.FirebaseConfig.retrieveRealtimeDatabaseOnListener

class InitializeBottomSheetCategories {
    fun initializeBottomSheetCategories(
        context: Context,
        cart: GeneralCategory,
        findNavController: NavController,
    ) {
        MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            customView(R.layout.bottom_sheet_category)
            cornerRadius(literalDp = 20f)

            val db = FirebaseDatabase.getInstance()
            val auth = FirebaseAuth.getInstance()

            val titleBottomSheet = view.findViewById<TextView>(R.id.titleBottomSheet)
            val closeBottomSheet = view.findViewById<ImageView>(R.id.closeBottomSheet)

            titleBottomSheet.text = cart.titleCategory
            closeBottomSheet.setOnClickListener { dismiss() }

            val medicalProfessionalsAdaptor = MedicalProfessionsAdaptor(context, auth, db, findNavController, this)
            when (cart.titleCategory) {
                "Documents" -> {
                    if (auth.currentUser?.uid != null) {
                        initializeDocumentsSection(context, db, auth, this)
                    } else {
                        dismiss()
                        cancel()
                        MoluccusToast(context).showInformation("Please Login In Order To Use This Feature!!")
                    }
                }

                "Pharmacy" -> {
                    PharmacyCategory().intitializePharmacy(this, db)
                }

                "Patients" -> {
                    GeneralStatistics().intitializeGeneralStatistics(this, db)
                }

                "Professionals" -> {
                    initializeProfessionalsSection(
                        this,
                        db,
                        medicalProfessionalsAdaptor
                    )
                }
                "Delivery" -> {
                    if (auth.currentUser?.uid != null) {
                        // todo:
                    } else {
                        dismiss()
                        cancel()
                        MoluccusToast(context).showInformation("Please Login In Order To Use This Feature!!")
                    }
                }

                else -> {
                    dismiss()
                }
            }
        }
    }

    private fun initializeDocumentsSection(
        context: Context,
        db: FirebaseDatabase,
        auth: FirebaseAuth,
        view: MaterialDialog
    ) {
        view.findViewById<RelativeLayout>(R.id.medicalDocuments).show()
        val medicalRec = mutableListOf<GeneralDocuments>()
        val recyclerViewDocuments = view.findViewById<RecyclerView>(R.id.recyclerViewDocuments)
        val searchDocuments = view.findViewById<TextInputEditText>(R.id.searchDocuments)

        recyclerViewDocuments.layoutManager = GridLayoutManager(context, 2)
        val medicalDocumentsAdaptor = MedicalDocumentsAdaptor(context)
        recyclerViewDocuments.adapter = medicalDocumentsAdaptor

        searchDocuments.doAfterTextChanged {
            val queries = searchDocuments.text
            if (!queries.isNullOrEmpty()) {
                val matchingResults = medicalRec.filter { result ->
                    result.documentShortDescription.orEmpty().contains(
                        queries.toString(),
                        ignoreCase = true
                    ) || result.documentTitle.orEmpty().contains(
                        queries.toString(),
                        ignoreCase = true
                    ) || result.documentDate.orEmpty().contains(
                        queries.toString(),
                        ignoreCase = true
                    )
                }

                when {
                    matchingResults.isEmpty() -> {
                        recyclerViewDocuments.loadSkeleton {
                            val customShimmer = Shimmer.AlphaHighlightBuilder()
                                .setDirection(Shimmer.Direction.TOP_TO_BOTTOM)
                                .build()
                            shimmer(customShimmer)
                        }
                        medicalDocumentsAdaptor.setData(medicalRec)
                    }

                    else -> {
                        recyclerViewDocuments.hideSkeleton()
                        medicalDocumentsAdaptor.setData(matchingResults.toMutableList())
                    }
                }
            } else {
                recyclerViewDocuments.hideSkeleton()
                medicalDocumentsAdaptor.setData(medicalRec)
            }
        }
        retrieveRealtimeDatabaseOnListener(
            db,
            "MedicalDocuments/${auth.currentUser!!.uid}",
            context
        ) { snapshot ->
            medicalRec.clear()
            for (dataSnapshot in snapshot.children) {
                val mdReadme = dataSnapshot.getValue(GeneralDocuments::class.java)
                mdReadme?.let { medicalRec.add(it) }
            }
            medicalDocumentsAdaptor.setData(medicalRec)
        }

    }

    private fun initializeProfessionalsSection(
        view: MaterialDialog,
        db: FirebaseDatabase,
        medicalProfessionalsAdaptor: MedicalProfessionsAdaptor
    ) {
        view.findViewById<LinearLayout>(R.id.medicalProfessional).show()
        val recyclerViewProfessionals = view.findViewById<RecyclerView>(R.id.recyclerViewProfessionals)
        val searchMedicalProfessions = view.findViewById<TextInputEditText>(R.id.searchMedicalProfessions)
        val chipGroup = view.findViewById<ChipGroup>(R.id.availableCategories)

        val medicalProfessionalsArray = view.windowContext.resources.getStringArray(R.array.medical_professionals)
        for (profession in medicalProfessionalsArray) {
            val chip = Chip(view.windowContext)
            chip.text = profession
            chip.isCheckable = true
            chipGroup.addView(chip)
        }

        view.onShow {
            recyclerViewProfessionals.layoutManager = GridLayoutManager(view.windowContext, 2)
            recyclerViewProfessionals.adapter = medicalProfessionalsAdaptor
        }

        view.onPreShow {
            val mdProfessionalsList = mutableListOf<Profiles>()
            retrieveRealtimeDatabaseOnListener(db, "participants", view.windowContext) { snapshot ->
                mdProfessionalsList.clear()
                for (snapshotItem in snapshot.children) {
                    val checkIfUserIsAccountTypeDoctor = snapshotItem.child("accountType").getValue(String::class.java)
                    if (checkIfUserIsAccountTypeDoctor == "doctor" || checkIfUserIsAccountTypeDoctor == "nurse") {
                        val mdProfessionalsData = snapshotItem.getValue(Profiles::class.java)
                        mdProfessionalsData?.let { mdProfessionalsList.add(it) }
                    }
                }
                searchMedicalProfessions.addTextChangedListener {
                    val matchingResults = mdProfessionalsList.filter { result ->
                        result.medicalProfessionals.medicalProfessionType.orEmpty().contains(
                            it?.trim().toString(),
                            ignoreCase = true
                        )
                    }
                    when {
                        it?.isNotEmpty() == true -> {
                            medicalProfessionalsAdaptor.setData(matchingResults.toMutableList())
                        }

                        else -> {
                            medicalProfessionalsAdaptor.setData(mdProfessionalsList)
                        }
                    }
                }
                medicalProfessionalsAdaptor.setData(mdProfessionalsList)
            }
        }
    }
}