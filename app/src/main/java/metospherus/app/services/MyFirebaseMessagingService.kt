package metospherus.app.services

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import metospherus.app.R

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            val reminderTitle = remoteMessage.data["title"]

            val title = remoteMessage.notification?.title?:""
            val message = remoteMessage.notification?.body?:""
            val type = remoteMessage.data["type"] ?: ""
            val id = remoteMessage.data["id"] ?: "0"
            val image = remoteMessage.notification?.imageUrl
            val bitmap: Bitmap? = null

            NotificationHelper(this).createNotification(title, message, id.toInt(), bitmap, type)
            if (reminderTitle != null) {
                showNotification(reminderTitle)
            }
        }
    }
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful && !userId.isNullOrEmpty()) {
                val userFCMToken = task.result
                FirebaseDatabase.getInstance().getReference("participants").child(userId)
                    .child("fcmToken").setValue(userFCMToken)
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun showNotification(reminderTitle: String) {
        val context = applicationContext
        val channelId = "metospherus_reminders"
        val notificationId = 123 // Unique ID for the notification

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(reminderTitle)
            .setContentText("Reminder to take Your $reminderTitle medication \uD83D\uDC8A")
            .setSmallIcon(R.drawable.splash_logo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, notificationBuilder.build())
        } catch (e: Exception) {
            Log.e("AlertReceiver", "Error showing notification", e)
        }
    }
}
