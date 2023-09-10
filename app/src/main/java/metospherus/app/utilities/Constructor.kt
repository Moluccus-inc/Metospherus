package metospherus.app.utilities

import android.app.AlarmManager
import android.os.Handler
import android.os.Looper
import android.view.View
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import metospherus.app.database.localhost.AppDatabase
import metospherus.app.database.profile_data.Profiles
import metospherus.app.modules.GeneralBrainResponse
import metospherus.app.modules.GeneralMenstrualCycle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

object Constructor {
    const val EXTRA_NOTIFICATION_ACTION = "extra_notification_action"
    const val ACTION_TAKEN = "action_taken"
    const val ACTION_NOT_YET = "action_not_yet"
    const val CHANNEL_ID = "channel_id"
    const val REQUEST_CODE = 170

    fun visibilityHelper(visibility: Boolean): Int {
        return when {
            visibility -> {
                View.VISIBLE
            }
            else -> {
                View.GONE
            }
        }
    }

    fun View.show() {
        visibility = View.VISIBLE
    }

    fun View.hide() {
        visibility = View.GONE
    }

    fun postDelayed(delayMillis: Long, task: () -> Unit) {
        Handler(Looper.myLooper()!!).postDelayed(task, delayMillis)
    }

    suspend fun insertOrUpdateUserProfile(userProfile: Profiles, appDatabase: AppDatabase) {
        withContext(Dispatchers.IO) {
            appDatabase.profileLocal().insertOrUpdateUserPatient(userProfile)
        }
    }
    suspend fun getUserProfilesFromDatabase(appDatabase: AppDatabase): Profiles? {
        return appDatabase.profileLocal().getUserPatient()
    }
    suspend fun getMenstrualCyclesFromLocalDatabase(appDatabase: AppDatabase): GeneralMenstrualCycle? {
        return appDatabase.menstrualCycleLocal().getMenstrualCycles()
    }
    suspend fun insertOrUpdateMenstrualCycles(menstrual: GeneralMenstrualCycle, appDatabase: AppDatabase) {
        withContext(Dispatchers.IO) {
            appDatabase.menstrualCycleLocal().insertOrUpdateMenstrualCycles(menstrual)
        }
    }
    suspend fun insertOrUpdateUserCompanionShip(userCompanionship: GeneralBrainResponse, appDatabase: AppDatabase) {
        withContext(Dispatchers.IO) {
            appDatabase.generalBrainResponse().insertOrUpdateUserCompanionShip(userCompanionship)
        }
    }
    suspend fun getCompanionShipFromLocalDatabase(appDatabase: AppDatabase): GeneralBrainResponse? {
        return appDatabase.generalBrainResponse().getUserCompanionShip()
    }
    fun generateRandomIdWithDateTime(): String {
        val currentTimeMillis = System.currentTimeMillis()
        val random = Random.nextLong()
        val combinedId = currentTimeMillis + random
        val sdf = SimpleDateFormat("dd MMM, yyyy - hh:mm a", Locale.ENGLISH)
        return sdf.format(Date(combinedId))
    }

    fun parseTimestampFromString(dateTimeString: String): Long? {
        try {
            val timestampString = dateTimeString.split(" - ")[0] // Extract the timestamp part
            return timestampString.toLong()
        } catch (e: NumberFormatException) {
            // Handle the case where parsing fails
            e.printStackTrace()
        }
        return null
    }

    fun convertTimeToMilliseconds(time: String): Long {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = sdf.parse(time)
        val calendar = java.util.Calendar.getInstance()
        date?.let {
            calendar.time = it
            calendar.set(java.util.Calendar.SECOND, 0)
            calendar.set(java.util.Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    fun getAlarmInterval(dateLayout: String): Long {
        return when (dateLayout) {
            "Day" -> AlarmManager.INTERVAL_DAY
            "Week" -> AlarmManager.INTERVAL_DAY * 7
            "Month" -> AlarmManager.INTERVAL_DAY * 30 // Adjust as needed
            else -> AlarmManager.INTERVAL_DAY
        }
    }

    fun getGeneralLocationArea() {

    }
}