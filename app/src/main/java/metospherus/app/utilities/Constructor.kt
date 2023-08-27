package metospherus.app.utilities

import android.app.AlarmManager
import android.os.Handler
import android.os.Looper
import android.view.View
import metospherus.app.database.localhost.AppDatabase
import metospherus.app.database.profile_data.Profiles
import java.text.SimpleDateFormat
import java.util.Locale

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

    suspend fun getUserProfilesFromDatabase(appDatabase: AppDatabase): Profiles? {
        return appDatabase.profileLocal().getUserPatient()
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

}