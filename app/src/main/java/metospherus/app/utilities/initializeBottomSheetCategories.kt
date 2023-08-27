package metospherus.app.utilities

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import metospherus.app.R
import metospherus.app.adaptors.MedicalDocumentsAdaptor
import metospherus.app.fragments.HomeFragment
import metospherus.app.modules.GeneralCategory
import metospherus.app.modules.GeneralDocuments
import metospherus.app.modules.GeneralTemplate
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
            val medicalDocuments = view.findViewById<RelativeLayout>(R.id.medicalDocuments)

            titleBottomSheet.text = cart.titleCategory

            medicalDocumentsAdaptor = MedicalDocumentsAdaptor(context)
            val db = FirebaseDatabase.getInstance()
            val auth = FirebaseAuth.getInstance()
            when (cart.titleCategory) {
                "Documents" -> {
                    medicalDocuments.show()

                    val recyclerViewDocuments = view.findViewById<RecyclerView>(R.id.recyclerViewDocuments)
                    val searchDocuments = view.findViewById<TextInputEditText>(R.id.searchDocuments)
                    val addMoreMedicalDocuments = view.findViewById<ExtendedFloatingActionButton>(R.id.addMoreMedicalDocuments)

                    recyclerViewDocuments.layoutManager = GridLayoutManager(context, 2)

                    val medicalRecordsDB = db.getReference("MedicalDocuments").child(auth.currentUser!!.uid)
                    medicalRecordsDB.keepSynced(true)
                    medicalRecordsDB.addValueEventListener(object : ValueEventListener {
                        val medicalRec = mutableListOf<GeneralDocuments>()
                        override fun onDataChange(snapshot: DataSnapshot) {
                            medicalRec.clear()
                            for (dataSnapshot in snapshot.children) {
                                val mdReadme = dataSnapshot.getValue(GeneralDocuments::class.java)
                                if (mdReadme != null) {
                                    medicalRec.add(mdReadme)
                                }
                            }

                            medicalDocumentsAdaptor.setData(medicalRec)
                            recyclerViewDocuments.adapter = medicalDocumentsAdaptor
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
                }

                "Pharmacy" -> {
                    medicalDocuments.hide()
                }

                else -> {
                    medicalDocuments.hide()
                }
            }
        }
    }

}