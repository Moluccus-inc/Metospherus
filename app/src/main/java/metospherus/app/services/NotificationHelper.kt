package metospherus.app.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import metospherus.app.MainActivity
import metospherus.app.R
import metospherus.app.utilities.Constructor.CHANNEL_ID

class NotificationHelper(private val context: Context) {
    @SuppressLint("MissingPermission")
    fun createNotification(title: String, message: String) {
        createNotificationChannel(message, title)
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val icon = BitmapFactory.decodeResource(context.resources, R.drawable.medicine_reminder)

        val vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000, 1000, 0)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setLargeIcon(icon)
            .setContentTitle(title)
            .setContentText(message)
            .setVisibility(VISIBILITY_PUBLIC)
            .setStyle(
                NotificationCompat.BigPictureStyle().bigPicture(icon)
            )
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setVibrate(vibrationPattern) // Set custom vibration pattern
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(666, notification)
        }
    }
    private fun createNotificationChannel(message: String, title: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT)
                    .apply {
                        name = title
                        description = message
                    }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
