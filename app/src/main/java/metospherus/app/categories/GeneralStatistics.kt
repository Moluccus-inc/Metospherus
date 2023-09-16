package metospherus.app.categories

import android.widget.LinearLayout
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.FirebaseDatabase
import metospherus.app.R
import metospherus.app.database.profile_data.Profiles
import metospherus.app.utilities.Constructor.show
import metospherus.app.utilities.FirebaseConfig.retrieveRealtimeDatabaseOnListener

class GeneralStatistics {
    fun intitializeGeneralStatistics(
        viewDailog: MaterialDialog,
        db: FirebaseDatabase
    ) {
        viewDailog.findViewById<LinearLayout>(R.id.patientsStatistics).show()
        val patientsCount = viewDailog.findViewById<TextView>(R.id.patientsCount)
        val totalParticipants = viewDailog.findViewById<TextView>(R.id.totalParticipants)
        val facilitiesCount = viewDailog.findViewById<TextView>(R.id.facilitiesCount)
        val medicalProfessionalsCount = viewDailog.findViewById<TextView>(R.id.medicalProfessionalsCount)


        retrieveRealtimeDatabaseOnListener(db, "participants", viewDailog.windowContext, onDataChange = { dataSnapShot ->
            totalParticipants.text = dataSnapShot.childrenCount.toString()
            var patientCount = 0
            var facilityCount = 0
            var medicalProfessionals = 0
            for (dataSnapshotValue in dataSnapShot.children) {
                val valueReadable = dataSnapshotValue.getValue(Profiles::class.java)
                val accountType = valueReadable?.accountType ?: ""

                if (accountType.contains("patient", ignoreCase = true)) {
                    patientCount++
                }

                if (accountType.contains("pharmacy", ignoreCase = true)
                    || accountType.contains("hospital" , ignoreCase = true) || accountType.contains("organizations", ignoreCase = true)) {
                    facilityCount++
                }

                if (accountType.contains("doctor", ignoreCase = true) || accountType.contains("nurse", ignoreCase = true)) {
                    medicalProfessionals++
                }
            }
            patientsCount.text = patientCount.toString()
            facilitiesCount.text = facilityCount.toString()
            medicalProfessionalsCount.text = medicalProfessionals.toString()
        })
    }
}