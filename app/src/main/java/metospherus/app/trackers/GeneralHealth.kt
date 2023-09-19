package metospherus.app.trackers

import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onPreShow
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import metospherus.app.R
import metospherus.app.database.profile_data.GeneralUserInformation
import metospherus.app.utilities.Constructor.dpToPx
import metospherus.app.utilities.Constructor.hide
import metospherus.app.utilities.Constructor.show
import metospherus.app.utilities.FirebaseConfig.retrieveRealtimeDatabaseOnListener

class GeneralHealth {
    fun generalHealthModule(
        viewDialogSheet: MaterialDialog,
        auth: FirebaseAuth,
        db: FirebaseDatabase,
    ) {
        val medicalGeneralHealthModule =
            viewDialogSheet.findViewById<NestedScrollView>(R.id.medicalGeneralHealthModule)
        medicalGeneralHealthModule.show()

        val heartPulse = viewDialogSheet.findViewById<TextView>(R.id.heartPulse)
        val weight = viewDialogSheet.findViewById<TextView>(R.id.weight)
        val temperature = viewDialogSheet.findViewById<TextView>(R.id.temperature)
        val bloodGroup = viewDialogSheet.findViewById<TextView>(R.id.bloodGroup)
        val avgSleepTime = viewDialogSheet.findViewById<TextView>(R.id.avgSleepTime)
        val pillsAvailable = viewDialogSheet.findViewById<TextView>(R.id.pillsAvailable)
        val stepsTaken = viewDialogSheet.findViewById<TextView>(R.id.stepsTaken)
        val timeTakenOnDevice = viewDialogSheet.findViewById<TextView>(R.id.timeTakenOnDevice)

        // Moods Values
        val monday = viewDialogSheet.findViewById<MaterialCardView>(R.id.mondayMoods)
        val tuesday = viewDialogSheet.findViewById<MaterialCardView>(R.id.tuesdayMoods)
        val wednesday = viewDialogSheet.findViewById<MaterialCardView>(R.id.wednesdayMoods)
        val thursday = viewDialogSheet.findViewById<MaterialCardView>(R.id.thursdayMoods)
        val friday = viewDialogSheet.findViewById<MaterialCardView>(R.id.fridayMoods)
        val saturday = viewDialogSheet.findViewById<MaterialCardView>(R.id.saturdayMoods)
        val sunday = viewDialogSheet.findViewById<MaterialCardView>(R.id.sundayMoods)

        viewDialogSheet.setOnDismissListener {
            medicalGeneralHealthModule.hide()
        }

        viewDialogSheet.onPreShow {
            auth.currentUser?.uid.let { authUser ->
                retrieveRealtimeDatabaseOnListener(
                    db,
                    "participants/$authUser",
                    viewDialogSheet.windowContext
                ) { dataSnapshot ->
                    if (dataSnapshot.exists()) {
                        val dataSnapshotValue = dataSnapshot.getValue(GeneralUserInformation::class.java)

                        weight.text = (dataSnapshotValue?.generalHealthInformation?.weightRecord ?: "0").toString()
                        bloodGroup.text = (dataSnapshotValue?.generalHealthInformation?.bloodGroupRecord ?: "Unknown").toString()
                        heartPulse.text = (dataSnapshotValue?.generalHealthInformation?.heartPulseRecord ?: "0").toString()
                        temperature.text = (dataSnapshotValue?.generalHealthInformation?.temperatureRecord ?: "0").toString()
                        avgSleepTime.text = (dataSnapshotValue?.generalHealthInformation?.sleepTimeRecord ?: "0").toString()
                        pillsAvailable.text = (dataSnapshotValue?.generalHealthInformation?.pillsAvailableRecord ?: "0").toString()
                        stepsTaken.text = (dataSnapshotValue?.generalHealthInformation?.stepsRecord ?: "0").toString()
                        timeTakenOnDevice.text = (dataSnapshotValue?.generalHealthInformation?.deviceUsageTimeRecord ?: "0").toString()

                        val mondayParams = monday.layoutParams as LinearLayout.LayoutParams
                        mondayParams.height = (dataSnapshotValue?.generalHealthInformation?.moodsRecord?.monday!!).toInt()
                        monday.layoutParams = mondayParams

                        val tuesdayParams = tuesday.layoutParams as LinearLayout.LayoutParams
                        tuesdayParams.height = (dataSnapshotValue.generalHealthInformation.moodsRecord.tuesday)!!.toInt()
                        tuesday.layoutParams = tuesdayParams

                        val wednesdayParams = wednesday.layoutParams as LinearLayout.LayoutParams
                        wednesdayParams.height = (dataSnapshotValue.generalHealthInformation.moodsRecord.wednesday)!!.toInt()
                        wednesday.layoutParams = wednesdayParams

                        val thursdayParams = thursday.layoutParams as LinearLayout.LayoutParams
                        thursdayParams.height = (dataSnapshotValue.generalHealthInformation.moodsRecord.thursday)!!.toInt()
                        wednesday.layoutParams = thursdayParams

                        val fridayParams = friday.layoutParams as LinearLayout.LayoutParams
                        fridayParams.height = (dataSnapshotValue.generalHealthInformation.moodsRecord.friday)!!.toInt()
                        friday.layoutParams = fridayParams

                        val saturdayParams = saturday.layoutParams as LinearLayout.LayoutParams
                        saturdayParams.height = (dataSnapshotValue.generalHealthInformation.moodsRecord.saturday)!!.toInt()
                        saturday.layoutParams = saturdayParams

                        val sundayParams = sunday.layoutParams as LinearLayout.LayoutParams
                        sundayParams.height = (dataSnapshotValue.generalHealthInformation.moodsRecord.sunday)!!.toInt()
                        sunday.layoutParams = sundayParams
                    }
                }
            }
        }

    }
}