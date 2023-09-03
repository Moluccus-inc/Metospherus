package metospherus.app.utilities

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.internal.main.DialogLayout
import com.facebook.shimmer.Shimmer
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton
import metospherus.app.R
import metospherus.app.adaptors.MedicalDocumentsAdaptor
import metospherus.app.adaptors.MedicalProfessionsAdaptor
import metospherus.app.database.profile_data.Profiles
import metospherus.app.modules.GeneralCategory
import metospherus.app.modules.GeneralDocuments
import metospherus.app.utilities.Constructor.hide
import metospherus.app.utilities.Constructor.show

class InitializeBottomSheetCategories {
    fun initializeBottomSheetCategories(
        context: Context,
        cart: GeneralCategory,
    ) {
        MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            customView(R.layout.bottom_sheet_category)
            cornerRadius(literalDp = 20f)

            val db = FirebaseDatabase.getInstance()
            val auth = FirebaseAuth.getInstance()

            val titleBottomSheet = view.findViewById<TextView>(R.id.titleBottomSheet)
            val closeBottomSheet = view.findViewById<ImageView>(R.id.closeBottomSheet)
            val medicalDocuments = view.findViewById<RelativeLayout>(R.id.medicalDocuments)
            val patientsStatistics = view.findViewById<LinearLayout>(R.id.patientsStatistics)
            val medicalProfessional = view.findViewById<LinearLayout>(R.id.medicalProfessional)

            titleBottomSheet.text = cart.titleCategory
            closeBottomSheet.setOnClickListener { dismiss() }

            val retrieveMedicalProfessions = db.getReference("participants")
            retrieveMedicalProfessions.keepSynced(true)

            val medicalProfessionalsAdaptor = MedicalProfessionsAdaptor(context, auth, db)
            when (cart.titleCategory) {
                "Documents" -> {
                    medicalDocuments.show()
                    patientsStatistics.hide()
                    medicalProfessional.hide()
                    initializeDocumentsSection(context, db, auth, view)
                }
                "Pharmacy" -> {
                    medicalDocuments.hide()
                    patientsStatistics.hide()
                    medicalProfessional.hide()
                }
                "Patients" -> {
                    patientsStatistics.show()
                    medicalDocuments.hide()
                    medicalProfessional.hide()
                }
                "Professionals" -> {
                    medicalDocuments.hide()
                    patientsStatistics.hide()
                    medicalProfessional.show()

                    initializeProfessionalsSection(
                        context,
                        view,
                        retrieveMedicalProfessions,
                        medicalProfessionalsAdaptor
                    )
                }
                else -> {
                    medicalDocuments.hide()
                    patientsStatistics.hide()
                    medicalProfessional.hide()
                }
            }
        }
    }
    private fun initializeDocumentsSection(
        context: Context,
        db: FirebaseDatabase,
        auth: FirebaseAuth,
        view: DialogLayout
    ) {
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
                    matchingResults.isNullOrEmpty() -> {
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
        auth.currentUser?.let { currentUser ->
            val medicalRecordsDB = db.getReference("MedicalDocuments").child(currentUser.uid)
            medicalRecordsDB.keepSynced(true)
            medicalRecordsDB.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    medicalRec.clear()
                    for (dataSnapshot in snapshot.children) {
                        val mdReadme = dataSnapshot.getValue(GeneralDocuments::class.java)
                        mdReadme?.let { medicalRec.add(it) }
                    }
                    medicalDocumentsAdaptor.setData(medicalRec)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        } ?: MoluccusToast(context).showInformation("Please Login In Order To Use This Feature!!")
    }
    private fun initializeProfessionalsSection(
        context: Context,
        view: DialogLayout,
        retrieveMedicalProfessions: DatabaseReference,
        medicalProfessionalsAdaptor: MedicalProfessionsAdaptor
    ) {
        val recyclerViewProfessionals = view.findViewById<RecyclerView>(R.id.recyclerViewProfessionals)
        val searchMedicalProfessions = view.findViewById<TextInputEditText>(R.id.searchMedicalProfessions)
        val chipGroup = view.findViewById<ChipGroup>(R.id.availableCategories)

        val medicalProfessionalsArray = context.resources.getStringArray(R.array.medical_professionals)
        for (profession in medicalProfessionalsArray) {
            val chip = Chip(context)
            chip.text = profession
            chip.isCheckable = true
            chipGroup.addView(chip)
        }

        recyclerViewProfessionals.layoutManager = GridLayoutManager(context, 2)
        recyclerViewProfessionals.adapter = medicalProfessionalsAdaptor

        val mdProfessionalsList = mutableListOf<Profiles>()
        retrieveMedicalProfessions.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
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

            override fun onCancelled(error: DatabaseError) {
                MoluccusToast(context).showError("Cancelled ${error.message}")
            }
        })
    }
}