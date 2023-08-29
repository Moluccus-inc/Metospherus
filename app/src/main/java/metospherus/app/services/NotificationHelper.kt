package metospherus.app.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import metospherus.app.MainActivity
import metospherus.app.R

class NotificationHelper(private val context: Context) {

    fun createNotification(
        title: String,
        message: String,
        itemId: Int,
        bitmap: Bitmap?,
        type: String
    ) {
        if (bitmap != null) {
            showNotification(title, itemId, buildNotification(createIntent(type), title, message))
        } else {
            showNotification(
                title,
                itemId,
                buildNotificationWithImage(createIntent(type), title, message, bitmap)
            )
        }
    }

    private fun showNotification(title: String, itemId: Int, mBuilder: NotificationCompat.Builder) {
        val mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannelId = "metospherus_reminders"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(notificationChannelId, title, importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.WHITE
            notificationChannel.enableVibration(true)

            mBuilder.setChannelId(notificationChannelId)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }

        mNotificationManager.notify(itemId, mBuilder.build())
    }

    private fun createIntent(type: String): PendingIntent {
        val resultIntent = when (type) {
            "metospherus_intent" -> Intent(context, MainActivity::class.java)
            else -> Intent(context, MainActivity::class.java)
        }

        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        return TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    private fun buildNotification(
        pendingIntent: PendingIntent,
        title: String,
        message: String
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, "metospherus_build_notification")
            .setSmallIcon(R.drawable.splash_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
    }

    private fun buildNotificationWithImage(
        pendingIntent: PendingIntent,
        title: String,
        message: String,
        image: Bitmap?
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, "metospherus_img")
            .setSmallIcon(R.drawable.splash_logo)
            .setLargeIcon(image)
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(image)
            )
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
    }
}