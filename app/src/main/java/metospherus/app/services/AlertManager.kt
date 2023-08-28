package metospherus.app.services

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AlertManager {
    private val TAG = "AlertManager"
    private val CHANNEL_ID = "alert_channel"
    private val CHANNEL_NAME = "Alert Channel"

    @SuppressLint("ScheduleExactAlarm")
    fun createAlert(
        context: Context,
        title: String,
        message: String,
        time: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val providedTime = sdf.parse(time) // Parse the provided time string
        val calendar = Calendar.getInstance()

        val providedCalendar = Calendar.getInstance()
        if (providedTime != null) {
            providedCalendar.time = providedTime
        }

        calendar.set(Calendar.HOUR_OF_DAY, providedCalendar.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, providedCalendar.get(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, 0) // Reset seconds to 0

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        val timeInMillis = calendar.timeInMillis // Calculate the timeInMillis

        val wakefulIntent = Intent(context, WakefulAlertReceiver::class.java)
        wakefulIntent.putExtra("title", title)
        wakefulIntent.putExtra("message", message)
        val wakefulPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            wakefulIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            wakefulPendingIntent
        )
        Log.d(TAG, "Alert scheduled")
    }
}