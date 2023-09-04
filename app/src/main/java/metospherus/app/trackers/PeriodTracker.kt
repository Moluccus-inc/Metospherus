package metospherus.app.trackers

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LifecycleCoroutineScope
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.datetime.datePicker
import com.afollestad.materialdialogs.internal.main.DialogLayout
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import metospherus.app.R
import metospherus.app.database.localhost.AppDatabase
import metospherus.app.utilities.Constructor
import metospherus.app.utilities.updateFirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PeriodTracker {
    fun periodTrackerModule(
        viewDialogSheet: DialogLayout,
        lifecycleScope: LifecycleCoroutineScope,
        appDatabase: AppDatabase,
        context: Context,
        auth: FirebaseAuth,
        db: FirebaseDatabase
    ) {
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

                val menstrualLastPeriodStartDate = view.findViewById<TextInputEditText>(R.id.menstrualLastPeriodStartDate)
                val menstrualLastPeriodEndDate = view.findViewById<TextInputEditText>(R.id.menstrualLastPeriodEndDate)
                val menstrualCycleLength = view.findViewById<TextInputEditText>(R.id.menstrualCycleLength)
                val menstrualLongestCycle = view.findViewById<TextInputEditText>(R.id.menstrualLongestCycle)
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