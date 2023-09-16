package metospherus.app.services

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import metospherus.app.MainActivity
import metospherus.app.R
import metospherus.app.utilities.Constructor
import metospherus.app.utilities.Constructor.ACTION_REPLY
import metospherus.app.utilities.Constructor.KEY_REPLY
import java.net.HttpURLConnection
import java.net.URL

class FirebaseMessagingServices : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        //sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.data.isNotEmpty().let {
            val title = remoteMessage.data["title"].toString()
            val message = remoteMessage.data["body"].toString()
            val notificationImage = remoteMessage.data["imageUrl"].toString()
            val otheruid = remoteMessage.data["otheruserUid"].toString()

            if(!isAppInForeground(this)) {
                showNotificationToUser(title, message, notificationImage, otheruid)
            } else {
                showNotificationToUser(title, message, notificationImage, otheruid)
            }
        }
    }

    private fun isAppInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val packageName = context.packageName
        val appProcesses = activityManager.runningAppProcesses ?: return false
        for (appProcess in appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                appProcess.processName == packageName
            ) {
                return true
            }
        }
        return false
    }

    @SuppressLint("MissingPermission", "NewApi")
    private fun showNotificationToUser(
        title: String?,
        message: String?,
        notificationImage: String?,
        otheruid: String
    ) {
        createNotificationChannel(message!!, title!!)
        val replyLabel = "Reply"
        val replyPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            createReplyIntent(),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val remoteInput = RemoteInput.Builder(KEY_REPLY)
            .setLabel(replyLabel)
            .build()

        val replyAction = NotificationCompat.Action.Builder(
            R.drawable.round_reply,
            replyLabel,
            replyPendingIntent
        )
            .addRemoteInput(remoteInput)
            .build()

        val imageBitmap = downloadImage(notificationImage)
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
        )
        val vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000, 1000, 0)
        val notification = NotificationCompat.Builder(this, Constructor.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setLargeIcon(imageBitmap)
            .setContentTitle(title)
            .setContentText(message)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setVibrate(vibrationPattern) // Set custom vibration pattern
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .build()

        with(NotificationManagerCompat.from(this)) {
            notify(676, notification)
        }
    }

    private fun createNotificationChannel(message: String, title: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    Constructor.CHANNEL_ID,
                    Constructor.CHANNEL_ID,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                    .apply {
                        name = title
                        description = message
                    }
            val notificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun downloadImage(imageUrl: String?): Bitmap? {
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val inputStream = connection.inputStream
            return BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun createReplyIntent(): Intent {
        val intent = Intent(this, NotificationReceiver::class.java)
        intent.action = ACTION_REPLY
        return intent
    }
}