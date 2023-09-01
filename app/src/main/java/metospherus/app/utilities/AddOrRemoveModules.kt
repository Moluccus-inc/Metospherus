package metospherus.app.utilities

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import metospherus.app.R
import metospherus.app.adaptors.AddOrRemoveModulesAdaptor
import metospherus.app.modules.GeneralTemplate

class AddOrRemoveModules {
    fun  addOrRemoveModules(context: Context, db: FirebaseDatabase, auth: FirebaseAuth) {
        MaterialDialog(context).show {
            customView(R.layout.add_or_remove_modules_layout)
            cornerRadius(literalDp = 20f)

            val closeMaterialDialog = view.findViewById<ImageView>(R.id.closeMaterialDialog)
            val recyclerViewMaterial = view.findViewById<RecyclerView>(R.id.recyclerViewMaterial)
            recyclerViewMaterial.layoutManager = GridLayoutManager(context, 3)
            val addOrRemoveModulesAdapter = AddOrRemoveModulesAdaptor(context, db, auth)

            recyclerViewMaterial.adapter = addOrRemoveModulesAdapter
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
                                modulesData.pushKey = snapshotItem.key.toString()
                                moduleTemps.add(modulesData)
                            }
                        }
                        addOrRemoveModulesAdapter.setData(moduleTemps)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        MoluccusToast(context).showError("Cancelled ${error.message}")
                    }
                })
            }
            closeMaterialDialog.setOnClickListener {
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
}