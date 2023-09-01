package metospherus.app.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Parcel
import android.text.Editable
import android.text.TextWatcher
import android.text.style.BackgroundColorSpan
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.date.dayOfMonth
import com.afollestad.date.month
import com.afollestad.date.totalDaysInMonth
import com.afollestad.date.year
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.bottomsheets.setPeekHeight
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.datetime.datePicker
import com.afollestad.materialdialogs.datetime.timePicker
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.DayViewDecorator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import metospherus.app.R
import metospherus.app.adaptors.MedicineIntakeAdaptor
import metospherus.app.database.localhost.AppDatabase
import metospherus.app.modules.GeneralMenstrualCycle
import metospherus.app.modules.GeneralPills
import metospherus.app.modules.GeneralTemplate
import metospherus.app.utilities.Constructor.getMenstrualCyclesFromLocalDatabase
import metospherus.app.utilities.Constructor.hide
import metospherus.app.utilities.Constructor.insertOrUpdateMenstrualCycles
import metospherus.app.utilities.Constructor.show
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@SuppressLint("SetTextI18n")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(DelicateCoroutinesApi::class)
fun bottomSheetGeneral(
    context: Context,
    generalTemplate: GeneralTemplate,
    lifecycleScope: LifecycleCoroutineScope
) {
    MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
        customView(R.layout.general_bottom_sheet)
        cancelOnTouchOutside(false)
        cornerRadius(20f)

        lateinit var recyclerViewTracker: RecyclerView
        val appDatabase = AppDatabase.getInstance(context)

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

        val auth: FirebaseAuth = Firebase.auth
        val db: FirebaseDatabase = FirebaseDatabase.getInstance()

        val medicineIntakeAdapter = MedicineIntakeAdaptor(context, auth, db)
        when (generalTemplate.name?.trim()?.lowercase(Locale.ROOT)) {
            "Medical Intake".trim().lowercase(Locale.ROOT) -> {
                containerMedicalIntake.show()
                containerPeriodTracker.hide()

                val medicineCount = view.findViewById<Chip>(R.id.medicineCount)

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
                                            "medicineAmount" to medicineAmount.text!!.trim()
                                                .toString(),
                                            "medicineDate" to medicineDate.text!!.trim().toString(),
                                            "medicineTime" to medicineTime.text!!.trim().toString(),
                                            "medicineQuantity" to medicineQuantity.text!!.trim()
                                                .toString(),
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
                        LinearLayoutManager.VERTICAL,
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
                                    val modulesData =
                                        dataSnapshot.getValue(GeneralPills::class.java)
                                    medicineCount.text = snapshot.childrenCount.toString()
                                    if (modulesData != null) {
                                        modulesData.pushkey = dataSnapshot.key ?: ""
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

                val setPeriodInCalendar = findViewById<Chip>(R.id.setPeriodInCalander)
                val longestCycleView = findViewById<TextView>(R.id.longestCycleView)
                val periodLengthView = findViewById<TextView>(R.id.periodLengthView)
                val avarageCycleView = findViewById<TextView>(R.id.avarageCycleView)
                val nextPeriodIn = findViewById<TextView>(R.id.nextPeriodIn)
                val nextPeriodStartDate = findViewById<TextView>(R.id.nextPeriodStartDate)
                val nextFertilePhase = findViewById<TextView>(R.id.nextFertilePhase)
                val chancesOfPregnancy = findViewById<TextView>(R.id.chancesOfPregnancy)
                val cycleVariation = findViewById<TextView>(R.id.cycleVariation)

                lifecycleScope.launch {
                    val userMenstrualCycles = getMenstrualCyclesFromLocalDatabase(appDatabase)
                    userMenstrualCycles?.let {
                        val currentDateMillis = Calendar.getInstance().timeInMillis
                        val previousEndDateMillis = it.previous_end_date?.let { endDateStr ->
                            val endDate = SimpleDateFormat("MMM dd, yyyy", Locale.US).parse(endDateStr)
                            endDate?.time ?: 0
                        } ?: currentDateMillis

                        val averageCycleLengthMillis = it.cycle_length!!.toInt() * 24 * 60 * 60 * 1000
                        val nextPeriodStartDateMillis = previousEndDateMillis + averageCycleLengthMillis

                        //val currentDate = Calendar.getInstance()
                        val nextPeriodStartDates = Calendar.getInstance()
                        nextPeriodStartDates.timeInMillis = nextPeriodStartDateMillis

                        val daysLeft = ((nextPeriodStartDateMillis - currentDateMillis) / (1000 * 60 * 60 * 24)).toInt()

                        avarageCycleView.text = it.cycle_length.toString()
                        val nextPeriodStartDateFormatted = SimpleDateFormat("dd. MMM", Locale.US).format(nextPeriodStartDates.time)
                        nextPeriodStartDate.text = nextPeriodStartDateFormatted

                        nextPeriodIn.text = "$daysLeft Days"

                        val nextFertileStartDateMillis = nextPeriodStartDateMillis - (averageCycleLengthMillis * 0.4).toLong()
                        val nextFertileStartFormatted =
                            SimpleDateFormat("dd. MMM", Locale.US).format(nextFertileStartDateMillis)
                        nextFertilePhase.text = nextFertileStartFormatted

                        val safeLowFertilityStartMillis = nextFertileStartDateMillis - (averageCycleLengthMillis * 0.2).toLong()
                        val safeHighFertilityEndMillis = nextFertileStartDateMillis + (averageCycleLengthMillis * 0.2).toLong()

                        val safetyLevel = when {
                            currentDateMillis < safeLowFertilityStartMillis -> "Low"
                            currentDateMillis <= safeHighFertilityEndMillis -> "Medium"
                            else -> "High"
                        }
                        chancesOfPregnancy.text = safetyLevel

                        val cycleVariationMillis =
                            (it.longest_cycle!!.toInt() - it.cycle_length.toInt()) * 24 * 60 * 60 * 1000
                        val cycleVariationDays = (cycleVariationMillis / (1000 * 60 * 60 * 24))
                        cycleVariation.text = "$cycleVariationDays days"

                        periodLengthView.text = "${it.cycle_length} days"
                        longestCycleView.text = "${it.longest_cycle} days"
                    }
                }

                setPeriodInCalendar.setOnClickListener {
                    MaterialDialog(context).show {
                        customView(R.layout.add_mentro_informations)
                        cornerRadius(literalDp = 20f)

                        val menstrualLastPeriodStartDate =
                            view.findViewById<TextInputEditText>(R.id.menstrualLastPeriodStartDate)
                        val menstrualLastPeriodEndDate =
                            view.findViewById<TextInputEditText>(R.id.menstrualLastPeriodEndDate)
                        val menstrualCycleLength =
                            view.findViewById<TextInputEditText>(R.id.menstrualCycleLength)
                        val menstrualLongestCycle =
                            view.findViewById<TextInputEditText>(R.id.menstrualLongestCycle)
                        val menstrualAddNotes =
                            view.findViewById<TextInputEditText>(R.id.menstrualAddNotes)

                        menstrualLastPeriodStartDate.setOnClickListener {
                            MaterialDialog(context).show {
                                datePicker { _, datetime ->
                                    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
                                    menstrualLastPeriodStartDate.setText(dateFormat.format(datetime.time))
                                    updateFirebaseDatabase(
                                        "previous_start_date",
                                        dateFormat.format(datetime.time).toString(),
                                        auth,
                                        db
                                    )
                                }
                            }
                        }
                        menstrualLastPeriodEndDate.setOnClickListener {
                            MaterialDialog(context).show {
                                datePicker { _, datetime ->
                                    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
                                    menstrualLastPeriodEndDate.setText(dateFormat.format(datetime.time))
                                    updateFirebaseDatabase(
                                        "previous_end_date",
                                        dateFormat.format(datetime.time).toString(),
                                        auth,
                                        db
                                    )
                                }
                            }
                        }

                        menstrualCycleLength.doAfterTextChanged {
                            if (menstrualCycleLength.text!!.isEmpty()) {
                                it?.toString()?.let { inputText ->
                                    updateFirebaseDatabase(
                                        "cycle_length",
                                        inputText,
                                        auth,
                                        db
                                    )
                                }
                            }
                        }
                        menstrualLongestCycle.doAfterTextChanged {
                            if (menstrualLongestCycle.text!!.isNotEmpty()) {
                                it?.toString()?.let { inputText ->
                                    updateFirebaseDatabase("longest_cycle", inputText, auth, db)
                                }
                            }
                        }
                        menstrualAddNotes.doAfterTextChanged {
                            it?.toString()?.let { inputText ->
                                updateFirebaseDatabase("notes", inputText.trim(), auth, db)
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

                            lifecycleScope.launch {
                                val userMenstrualCycles =
                                    getMenstrualCyclesFromLocalDatabase(appDatabase)
                                if (userMenstrualCycles != null) {
                                    menstrualLastPeriodStartDate.setText(userMenstrualCycles.previous_start_date)
                                    menstrualLastPeriodEndDate.setText(userMenstrualCycles.previous_end_date)

                                    menstrualCycleLength.setText(userMenstrualCycles.cycle_length)
                                    menstrualLongestCycle.setText(userMenstrualCycles.longest_cycle)
                                    menstrualAddNotes.setText(userMenstrualCycles.notes)
                                }
                            }
                        }
                    }
                }
            }

            "Fitness Health".trim().lowercase(Locale.ROOT) -> {
                containerMedicalIntake.hide()
                containerPeriodTracker.hide()
            }

            else -> {
                containerMedicalIntake.hide()
                containerPeriodTracker.hide()
            }
        }

        onShow {
            auth.currentUser?.let { currentUser ->
                val menstrualCyclesManager =
                    db.getReference("medicalmodules").child("userspecific").child("menstrual")
                        .child(currentUser.uid)
                menstrualCyclesManager.keepSynced(true)
                menstrualCyclesManager.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val menstrualCycles = snapshot.getValue(
                                GeneralMenstrualCycle::class.java
                            )
                            if (menstrualCycles != null) {
                                lifecycleScope.launch {
                                    insertOrUpdateMenstrualCycles(
                                        menstrualCycles,
                                        appDatabase
                                    )
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle the error if needed.
                    }
                })
            }
        }
    }
}

private fun updateFirebaseDatabase(
    id: String,
    value: String,
    auth: FirebaseAuth,
    db: FirebaseDatabase
) {
    if (value != "null" && value != "Not Set") {
        val databaseRef = db.getReference("medicalmodules")
            .child("userspecific").child("menstrual")
            .child(auth.currentUser!!.uid)
        databaseRef.child(id).setValue(value)
    }
}

fun calculateDaysBetween(startDate: Date, endDate: Date): Int {
    val diffInMillis = endDate.time - startDate.time
    return (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
}
