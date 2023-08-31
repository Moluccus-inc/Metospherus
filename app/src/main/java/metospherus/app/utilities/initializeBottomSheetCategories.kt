package metospherus.app.utilities

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.facebook.shimmer.Shimmer
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton
import metospherus.app.R
import metospherus.app.adaptors.MedicalDocumentsAdaptor
import metospherus.app.modules.GeneralCategory
import metospherus.app.modules.GeneralDocuments
import metospherus.app.utilities.Constructor.hide
import metospherus.app.utilities.Constructor.show

class InitializeBottomSheetCategories {

    private lateinit var medicalDocumentsAdaptor: MedicalDocumentsAdaptor
    fun initializeBottomSheetCategories(
        context: Context,
        cart: GeneralCategory,
    ) {
        MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            customView(R.layout.bottom_sheet_category)
            cornerRadius(literalDp = 20f)

            val titleBottomSheet = view.findViewById<TextView>(R.id.titleBottomSheet)
            val closeBottomSheet = view.findViewById<ImageView>(R.id.closeBottomSheet)
            val medicalDocuments = view.findViewById<RelativeLayout>(R.id.medicalDocuments)
            val patientsStatistics = view.findViewById<LinearLayout>(R.id.patientsStatistics)
            val medicalProfessional = view.findViewById<LinearLayout>(R.id.medicalProfessional)

            titleBottomSheet.text = cart.titleCategory
            closeBottomSheet.setOnClickListener {
                dismiss()
            }
            medicalDocumentsAdaptor = MedicalDocumentsAdaptor(context)
            val db = FirebaseDatabase.getInstance()
            val auth = FirebaseAuth.getInstance()
            when (cart.titleCategory) {
                "Documents" -> {
                    medicalDocuments.show()
                    patientsStatistics.hide()
                    medicalProfessional.hide()
                    val medicalRec = mutableListOf<GeneralDocuments>()

                    val recyclerViewDocuments =
                        view.findViewById<RecyclerView>(R.id.recyclerViewDocuments)
                    val searchDocuments = view.findViewById<TextInputEditText>(R.id.searchDocuments)

                    recyclerViewDocuments.layoutManager = GridLayoutManager(context, 2)
                    recyclerViewDocuments.adapter = medicalDocumentsAdaptor

                    searchDocuments.doAfterTextChanged {
                        val queries = searchDocuments.text
                        if (!queries.isNullOrEmpty()) {
                            val matchingResults = medicalRec
                                .filter { result ->
                                    result.documentShortDescription!!.contains(
                                        queries.toString(),
                                        ignoreCase = true
                                    ) or result.documentTitle!!.contains(
                                        queries.toString(),
                                        ignoreCase = true
                                    ) or result.documentDate!!.contains(
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
                    when {
                        auth.currentUser != null -> {
                            val medicalRecordsDB = db.getReference("MedicalDocuments").child(auth.currentUser!!.uid)
                            medicalRecordsDB.keepSynced(true)
                            medicalRecordsDB.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    medicalRec.clear()
                                    for (dataSnapshot in snapshot.children) {
                                        val mdReadme =
                                            dataSnapshot.getValue(GeneralDocuments::class.java)
                                        if (mdReadme != null) {
                                            medicalRec.add(mdReadme)
                                        }
                                    }
                                    medicalDocumentsAdaptor.setData(medicalRec)
                                }

                                override fun onCancelled(error: DatabaseError) {}
                            })
                        }

                        else -> {
                            MoluccusToast(context).showInformation("Please Login In Order To Use This Feature!!")
                        }
                    }
                }

                "Pharmacy" -> {
                    medicalDocuments.hide()
                    patientsStatistics.hide()
                    medicalProfessional.hide()
                }

                "Patients" -> {
                    medicalDocuments.hide()
                    patientsStatistics.show()
                    medicalProfessional.hide()
                }

                "Professionals" -> {
                    medicalProfessional.show()
                    medicalDocuments.hide()
                    patientsStatistics.hide()
                }

                else -> {
                    medicalDocuments.hide()
                }
            }
        }
    }

}