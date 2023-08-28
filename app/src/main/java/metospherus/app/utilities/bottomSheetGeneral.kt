package metospherus.app.utilities

import android.content.Context
import android.os.Build
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.datetime.timePicker
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import koleton.api.hideSkeleton
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import metospherus.app.R
import metospherus.app.adaptors.MedicineIntakeAdaptor
import metospherus.app.modules.GeneralPills
import metospherus.app.modules.GeneralTemplate
import metospherus.app.utilities.Constructor.hide
import metospherus.app.utilities.Constructor.show
import metospherus.app.utilities.Constructor.visibilityHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(DelicateCoroutinesApi::class)
fun bottomSheetGeneral(context: Context, generalTemplate: GeneralTemplate) {
    MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
        customView(R.layout.general_bottom_sheet)
        cancelOnTouchOutside(false)
        cornerRadius(20f)

        lateinit var recyclerViewTracker: RecyclerView

        val closeBottomSheet = view.findViewById<ImageView>(R.id.closeBottomSheet)
        val titleBottomSheet = view.findViewById<TextView>(R.id.titleBottomSheet)

        // Layouts
        val containerMedicalIntake = view.findViewById<RelativeLayout>(R.id.container_medical_intake)
        val containerPeriodTracker = view.findViewById<LinearLayout>(R.id.container_period_tracker)

        // fab
        val addMedicalInTake = view.findViewById<FloatingActionButton>(R.id.addMedicalInTake)

        titleBottomSheet.text = generalTemplate.name
        closeBottomSheet.setOnClickListener {
            dismiss()
        }

        var auth: FirebaseAuth = Firebase.auth
        var db: FirebaseDatabase = FirebaseDatabase.getInstance()

        var startDate: Date? = null
        var endDate: Date? = null

        val medicineIntakeAdapter = MedicineIntakeAdaptor(context, auth, db)
        when (generalTemplate.name?.trim()?.lowercase(Locale.ROOT)) {
            "Medical Intake".trim().lowercase(Locale.ROOT) -> {
                containerMedicalIntake.show()
                containerPeriodTracker.hide()

                val medicineCount = view.findViewById<Chip>(R.id.medicineCount)

                addMedicalInTake.setOnClickListener {
                    if (auth.currentUser !=null) {
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
                                val imageHolderOne =
                                    view.findViewById<MaterialCardView>(R.id.imageHolderOne)
                                val imageHolderTwo =
                                    view.findViewById<MaterialCardView>(R.id.imageHolderTwo)
                                val imageHolderThree =
                                    view.findViewById<MaterialCardView>(R.id.imageHolderThree)

                                val medicineName =
                                    view.findViewById<TextInputEditText>(R.id.medicine_name)
                                val medicineAmount =
                                    view.findViewById<AutoCompleteTextView>(R.id.medicine_amount)
                                val medicineDate =
                                    view.findViewById<AutoCompleteTextView>(R.id.medicine_date)
                                val medicineQuantity =
                                    view.findViewById<TextInputEditText>(R.id.medicine_quantity)
                                val medicineTime =
                                    view.findViewById<TextInputEditText>(R.id.medicine_time)

                                val imageHolders =
                                    listOf(imageHolderOne, imageHolderTwo, imageHolderThree)
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

                recyclerViewTracker = view.findViewById(R.id.recyclerViewBottomSheet)
                recyclerViewTracker.layoutManager =
                    LinearLayoutManager(
                        context,
                        LinearLayoutManager.HORIZONTAL,
                        false
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
                                    if (modulesData != null) {
                                        modulesData.pushkey = dataSnapshot.key?: ""
                                        moduleTemps.add(modulesData)
                                    }
                                }
                                medicineIntakeAdapter.setData(moduleTemps)
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

            "Period Tracker".trim().lowercase(Locale.ROOT) -> {
                containerPeriodTracker.show()
                containerMedicalIntake.hide()

                val calendarView = findViewById<CalendarView>(R.id.calendarView)

                calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }.time

                    if (startDate == null) {
                        startDate = selectedDate
                        Toast.makeText(
                            context,
                            "Start date selected: $selectedDate",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (endDate == null) {
                        endDate = selectedDate
                        Toast.makeText(context, "End date selected: $selectedDate", Toast.LENGTH_SHORT)
                            .show()

                        // Handle the date range, e.g., calculate days between start and end dates
                        val daysBetween = calculateDaysBetween(startDate!!, endDate!!)
                        Toast.makeText(context, "Days between: $daysBetween", Toast.LENGTH_SHORT)
                            .show()

                        // Clear the selected dates for the next range selection
                        startDate = null
                        endDate = null
                    }
                }
            }

            else -> {
                containerMedicalIntake.hide()
                containerPeriodTracker.hide()
            }
        }
    }
}

fun calculateDaysBetween(startDate: Date, endDate: Date): Int {
    val diffInMillis = endDate.time - startDate.time
    return (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
}
