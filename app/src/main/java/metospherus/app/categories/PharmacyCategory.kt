package metospherus.app.categories

import android.widget.LinearLayout
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton
import metospherus.app.R
import metospherus.app.adaptors.PharmaciesAdaptor
import metospherus.app.modules.GeneralPharmaciesSales
import metospherus.app.utilities.Constructor.show
import metospherus.app.utilities.FirebaseConfig.retrieveRealtimeDatabaseOnListener

class PharmacyCategory() {
    fun intitializePharmacy(
        viewDailog: MaterialDialog,
        db: FirebaseDatabase
    ) {
        val medicalPharmacyCategory = viewDailog.view.findViewById<LinearLayout>(R.id.medicalPharmacyCategory)
        medicalPharmacyCategory.show()
        val pharmaciesAdaptor = PharmaciesAdaptor(viewDailog.windowContext)
        val searchMedicineByNameOfDescription = viewDailog.view.findViewById<TextInputEditText>(R.id.searchMedicineByNameOfDescription)
        val pharmacyMedicineRecyclerView = viewDailog.view.findViewById<RecyclerView>(R.id.pharmacyMedicineRecyclerView)
        pharmacyMedicineRecyclerView.layoutManager = GridLayoutManager(viewDailog.context, 2)

        pharmacyMedicineRecyclerView.adapter = pharmaciesAdaptor
        pharmacyMedicineRecyclerView.loadSkeleton(R.layout.template_medical_shopping_card) {
            itemCount(5)
        }
        retrieveRealtimeDatabaseOnListener(
            db,
            "pharmacy",
            viewDailog.context,
            onDataChange = { dataSnapshot ->
                val pharmacyMedicineSales = mutableListOf<GeneralPharmaciesSales>()
                for (dataSnapshotChildren in dataSnapshot.children) {  // first children
                    pharmacyMedicineSales.clear()
                    for (drugsOnSale in dataSnapshotChildren.children.reversed()) { // second children
                        val pharmaciesSales =
                            drugsOnSale.getValue(GeneralPharmaciesSales::class.java)
                        pharmaciesSales!!.pharmacyUid = dataSnapshotChildren.key
                        pharmacyMedicineSales.add(pharmaciesSales)
                    }
                    searchMedicineByNameOfDescription.doAfterTextChanged { editableValue ->
                        val matches = pharmacyMedicineSales.filter { result ->
                            result.name.orEmpty().contains(
                                editableValue?.trim().toString(),
                                ignoreCase = true
                            ) || result.description.orEmpty().contains(
                                editableValue?.trim().toString(),
                                ignoreCase = true
                            )
                        }

                        when {
                            matches.isEmpty() -> {
                                pharmacyMedicineRecyclerView.loadSkeleton(R.layout.template_medical_shopping_card) {
                                    itemCount(5)
                                }
                            }

                            else -> {
                                pharmacyMedicineRecyclerView.hideSkeleton()
                                pharmaciesAdaptor.setData(matches.toMutableList())
                            }
                        }
                    }
                    pharmacyMedicineRecyclerView.hideSkeleton()
                    pharmaciesAdaptor.setData(pharmacyMedicineSales)
                }
            })
    }
}