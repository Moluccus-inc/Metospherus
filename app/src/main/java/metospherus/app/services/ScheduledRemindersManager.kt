package metospherus.app.services

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import metospherus.app.modules.GeneralReminders
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class ScheduledRemindersManager(
    private val context: Context
) {
    fun scheduleNotifications(reminderList: List<GeneralReminders>) {
        for (reminder in reminderList) {
            val reminderTime = reminder.schTime ?: continue // in format 10:20 PM/AM
            //val reminderDate = reminder.schDate ?: continue // In format Day, Week, Month
            val reminderTitle = reminder.schTitle ?: continue

            val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
            val parsedTime = timeFormat.parse(reminderTime)

            if (parsedTime == null) {
                continue
            }

            val calendar = Calendar.getInstance()
            calendar.time = parsedTime

            val userSelectedDateTime = Calendar.getInstance()
            userSelectedDateTime.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))
            userSelectedDateTime.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
            userSelectedDateTime.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
            userSelectedDateTime.set(Calendar.SECOND, 0)
            userSelectedDateTime.set(Calendar.MILLISECOND, 0)

            val todayDateTime = Calendar.getInstance()
            if (userSelectedDateTime.timeInMillis <= todayDateTime.timeInMillis) {
               // println("Skip scheduling time is in the past")
                continue
            }

            val delayInSeconds = TimeUnit.MILLISECONDS.toSeconds(userSelectedDateTime.timeInMillis - todayDateTime.timeInMillis)

            createWorkRequest(reminderTitle, delayInSeconds)
           // println("Reminder set to $reminderTitle : $delayInSeconds")
        }
    }

    private fun createWorkRequest(message: String,timeDelayInSeconds: Long  ) {
        val myWorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(timeDelayInSeconds, TimeUnit.SECONDS)
            .setInputData(workDataOf(
                "title" to message,
                "message" to "This is A Reminder to take your $message medicine in time \uD83D\uDE0A",
            ))
            .build()

        WorkManager.getInstance(context).enqueue(myWorkRequest)
    }
}