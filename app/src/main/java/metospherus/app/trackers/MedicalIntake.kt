package metospherus.app.trackers

import android.content.Context
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.datetime.timePicker
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import metospherus.app.R
import metospherus.app.adaptors.MedicineIntakeAdaptor
import metospherus.app.modules.GeneralPills
import metospherus.app.utilities.Constructor.show
import metospherus.app.utilities.FirebaseConfig.updateRealtimeDatabaseData
import metospherus.app.utilities.MoluccusToast
import metospherus.app.utilities.updateFirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Locale

class MedicalIntake {
    @OptIn(DelicateCoroutinesApi::class)
    fun medicalIntakeModule(
        viewDialogSheet: MaterialDialog,
        addMedicalInTake: FloatingActionButton,
        auth: FirebaseAuth,
        db: FirebaseDatabase,
        context: Context,
        recyclerViewTracker: RecyclerView,
        medicineIntakeAdapter: MedicineIntakeAdaptor
    ) {

        val containerMedicalIntake = viewDialogSheet.findViewById<RelativeLayout>(R.id.container_medical_intake)
        containerMedicalIntake.show()
        val medicineCount = viewDialogSheet.findViewById<Chip>(R.id.medicineCount)

        addMedicalInTake.setOnClickListener {
            if (auth.currentUser != null) {
                GlobalScope.launch(Dispatchers.Main) {
                    MaterialDialog(context).show {
                        customView(R.layout.dialog_container_medicine)
                        cornerRadius(literalDp = 20f)

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

                        // Cards
                        val imageHolderOne = view.findViewById<MaterialCardView>(R.id.imageHolderOne)
                        val imageHolderTwo = view.findViewById<MaterialCardView>(R.id.imageHolderTwo)
                        val imageHolderThree = view.findViewById<MaterialCardView>(R.id.imageHolderThree)

                        val medicineName = view.findViewById<TextInputEditText>(R.id.medicine_name)
                        val medicineAmount = view.findViewById<AutoCompleteTextView>(R.id.medicine_amount)
                        val medicineDate = view.findViewById<AutoCompleteTextView>(R.id.medicine_date)
                        val medicineQuantity = view.findViewById<TextInputEditText>(R.id.medicine_quantity)
                        val medicineTime = view.findViewById<TextInputEditText>(R.id.medicine_time)

                        val imageHolders = listOf(imageHolderOne, imageHolderTwo, imageHolderThree)
                        var selectedImageHolder: MaterialCardView? = null
                        var selectedCardNumber: String? = null

                        for ((index, imageHolder) in imageHolders.withIndex()) {
                            val cardNumber = (index + 1).toString()
                            imageHolder.setOnClickListener {
                                if (selectedImageHolder == imageHolder) {
                                    selectedImageHolder = null
                                    selectedCardNumber = null
                                    imageHolder.isChecked = false
                                } else {
                                    selectedImageHolder?.isChecked = false
                                    selectedImageHolder = imageHolder
                                    selectedCardNumber = cardNumber
                                    imageHolder.isChecked = true
                                }

                                // You can use the selectedCardNumber here as needed
                            }
                        }

                        medicineTime.setOnClickListener {
                            MaterialDialog(context).show {
                                timePicker(show24HoursView = false) { _, datetime ->
                                    val dateFormat =
                                        SimpleDateFormat("hh:mm a", Locale.getDefault())
                                    medicineTime.setText(dateFormat.format(datetime.time))
                                }
                            }
                        }
                        negativeButton(text = "Dismiss") {
                            dismiss()
                        }

                        positiveButton(text = "add medicine") {
                            if (!medicineName.text.isNullOrEmpty() && !medicineDate.text.isNullOrEmpty() && !medicineAmount.text.isNullOrEmpty()) {
                                val medicationReminder = mapOf(
                                    "medicineAvatar" to selectedCardNumber.toString(),
                                    "medicineName" to medicineName.text!!.trim().toString(),
                                    "medicineAmount" to medicineAmount.text!!.trim().toString(),
                                    "medicineDate" to medicineDate.text!!.trim().toString(),
                                    "medicineTime" to medicineTime.text!!.trim().toString(),
                                    "medicineQuantity" to medicineQuantity.text!!.trim().toString(),
                                )

                                db.getReference("medicalmodules").child("userspecific")
                                    .child("medicineIntake")
                                    .child(auth.currentUser!!.uid).push()
                                    .setValue(medicationReminder)
                                    .addOnCompleteListener {
                                        when {
                                            it.isSuccessful -> {
                                                dismiss()
                                            }

                                            else -> {
                                                MoluccusToast(windowContext).showInformation(
                                                    "Not added!! ${it.exception?.message}"
                                                )
                                            }
                                        }
                                    }
                            } else {
                                MoluccusToast(windowContext).showInformation("Not added!!")
                            }
                            MoluccusToast(context).showSuccess("Added medication")
                        }
                    }
                }
            } else {
                MoluccusToast(context).showSuccess("Please Login To Have A Better Experience With Metospherus")
            }
        }

        recyclerViewTracker.layoutManager = GridLayoutManager(
            context, 2
        )
        recyclerViewTracker.isNestedScrollingEnabled = true
        recyclerViewTracker.setHasFixedSize(true)

        when {
            auth.currentUser != null -> {
                val patientMedicineModulesDB = db.getReference("medicalmodules")
                    .child("userspecific")
                    .child("medicineIntake")
                    .child(auth.currentUser!!.uid)
                patientMedicineModulesDB.keepSynced(true)
                patientMedicineModulesDB.addValueEventListener(object : ValueEventListener {
                    val moduleTemps = mutableListOf<GeneralPills>()
                    override fun onDataChange(snapshot: DataSnapshot) {
                        moduleTemps.clear()
                        for (dataSnapshot in snapshot.children) {
                            val modulesData = dataSnapshot.getValue(GeneralPills::class.java)
                            medicineCount.text = snapshot.childrenCount.toString()
                            updateRealtimeDatabaseData(db, "participants/${auth.currentUser?.uid}/generalHealthInformation/pillsAvailableRecord", snapshot.childrenCount.toString())
                            if (modulesData != null) {
                                modulesData.pushkey = dataSnapshot.key ?: ""
                                moduleTemps.add(modulesData)
                            }
                        }
                        medicineIntakeAdapter.setData(moduleTemps.asReversed())
                        recyclerViewTracker.adapter = medicineIntakeAdapter
                    }

                    override fun onCancelled(error: DatabaseError) {
                        MoluccusToast(context).showError("Cancelled ${error.message}")
                    }
                })
            }

            else -> {
                MoluccusToast(context).showInformation("Please Login In Order To Use This Feature!!")
            }
        }
    }
}