package metospherus.app.trackers

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LifecycleCoroutineScope
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.datetime.datePicker
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import metospherus.app.R
import metospherus.app.database.localhost.AppDatabase
import metospherus.app.utilities.Constructor
import metospherus.app.utilities.Constructor.show
import metospherus.app.utilities.updateFirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PeriodTracker {
    @SuppressLint("SetTextI18n")
    fun periodTrackerModule(
        viewDialogSheet: MaterialDialog,
        lifecycleScope: LifecycleCoroutineScope,
        appDatabase: AppDatabase,
        context: Context,
        auth: FirebaseAuth,
        db: FirebaseDatabase
    ) {
        val containerPeriodTracker = viewDialogSheet.findViewById<LinearLayout>(R.id.container_period_tracker)
        containerPeriodTracker.show()


        val setPeriodInCalendar = viewDialogSheet.findViewById<Chip>(R.id.setPeriodInCalander)
        val longestCycleView = viewDialogSheet.findViewById<TextView>(R.id.longestCycleView)
        val periodLengthView = viewDialogSheet.findViewById<TextView>(R.id.periodLengthView)
        val avarageCycleView = viewDialogSheet.findViewById<TextView>(R.id.avarageCycleView)
        val nextPeriodIn = viewDialogSheet.findViewById<TextView>(R.id.nextPeriodIn)
        val nextPeriodStartDate = viewDialogSheet.findViewById<TextView>(R.id.nextPeriodStartDate)
        val nextFertilePhase = viewDialogSheet.findViewById<TextView>(R.id.nextFertilePhase)
        val chancesOfPregnancy = viewDialogSheet.findViewById<TextView>(R.id.chancesOfPregnancy)
        val cycleVariation = viewDialogSheet.findViewById<TextView>(R.id.cycleVariation)

        lifecycleScope.launch {
            val userMenstrualCycles = Constructor.getMenstrualCyclesFromLocalDatabase(appDatabase)
            userMenstrualCycles?.let { cycleValues ->
                val currentDateMillis = Calendar.getInstance().timeInMillis
                val previousEndDateMillis = cycleValues.previous_end_date?.let { endDateStr ->
                    val endDate = SimpleDateFormat("MMM dd, yyyy", Locale.US).parse(endDateStr)
                    endDate?.time ?: 0
                } ?: currentDateMillis

                val averageCycleLengthMillis = cycleValues.cycle_length!!.toInt() * 24 * 60 * 60 * 1000
                val nextPeriodStartDateMillis = previousEndDateMillis + averageCycleLengthMillis
                val currentDayMillis = currentDateMillis - nextPeriodStartDateMillis
                val currentDay = (currentDayMillis / (1000 * 60 * 60 * 24)).toInt()

                if (currentDay == 0) {
                    showCycleStartPopup(context, auth, db)
                    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
                    cycleValues.previous_start_date = dateFormat.format(currentDateMillis)
                    updateFirebaseDatabase(
                        "previous_start_date",
                        dateFormat.format(nextPeriodStartDateMillis).toString(),
                        auth,
                        db
                    )
                }

                val nextFertileStartDateMillis = nextPeriodStartDateMillis + (averageCycleLengthMillis * 0.4).toLong()
                val safeLowFertilityStartMillis = nextFertileStartDateMillis - (averageCycleLengthMillis * 0.2).toLong()
                val safeHighFertilityEndMillis = nextFertileStartDateMillis + (averageCycleLengthMillis * 0.2).toLong()

                val safetyLevel = when {
                    currentDateMillis < safeLowFertilityStartMillis -> "Low"
                    currentDateMillis <= safeHighFertilityEndMillis -> "Medium"
                    else -> "High"
                }

                val textColor = when {
                    currentDay in 1 until 5 -> Color.RED
                    (currentDay >= 5) && (currentDay <= cycleValues.cycle_length.toInt()) -> Color.YELLOW
                    else -> Color.LTGRAY
                }

                avarageCycleView.text = cycleValues.cycle_length.toString()
                val nextPeriodStartDateFormatted = SimpleDateFormat("dd. MMM", Locale.US).format(nextPeriodStartDateMillis)
                nextPeriodStartDate.text = nextPeriodStartDateFormatted
                nextPeriodIn.text = "$currentDay Days"
                nextFertilePhase.text = SimpleDateFormat("dd. MMM", Locale.US).format(nextFertileStartDateMillis)
                chancesOfPregnancy.text = safetyLevel
                cycleVariation.text = "${(cycleValues.longest_cycle!!.toInt() - cycleValues.cycle_length.toInt())} days"
                periodLengthView.text = "${cycleValues.cycle_length} days"
                longestCycleView.text = "${cycleValues.longest_cycle} days"

                avarageCycleView.setTextColor(textColor)
                nextPeriodStartDate.setTextColor(textColor)
                nextPeriodIn.setTextColor(textColor)
                nextFertilePhase.setTextColor(textColor)
                chancesOfPregnancy.setTextColor(textColor)
                cycleVariation.setTextColor(textColor)
                periodLengthView.setTextColor(textColor)
                longestCycleView.setTextColor(textColor)
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
                val menstrualAddNotes = view.findViewById<TextInputEditText>(R.id.menstrualAddNotes)

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
                            Constructor.getMenstrualCyclesFromLocalDatabase(appDatabase)
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
}

private fun showCycleStartPopup(context: Context, auth: FirebaseAuth, db: FirebaseDatabase) {
    val builder = MaterialAlertDialogBuilder(context) // 'this' should be your activity or context
    builder.setTitle("Menstrual Cycle Started")
    builder.setMessage("Your menstrual cycle has started. Update the start date?")

    builder.setPositiveButton("Yes") { _, _ ->
        val currentDateMillis = Calendar.getInstance().timeInMillis
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        val updatedStartDate = dateFormat.format(currentDateMillis)

        updateFirebaseDatabase("previous_start_date", updatedStartDate, auth, db)
    }

    builder.setNegativeButton("No") { _, _ ->
        // User clicked "No," do nothing or handle as needed
    }

    val dialog = builder.create()
    dialog.show()
}