package metospherus.app.services

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import metospherus.app.modules.GeneralReminders

class ScheduledRemindersManager(
    private val context: Context,
    private val auth: FirebaseAuth,
    private val db: FirebaseDatabase
) {

    fun scheduleNotificationsUsingFCM(reminderList: List<GeneralReminders>) {
        val userId = auth.currentUser?.uid ?: return

        db.getReference("participants").child(userId).child("fcmToken")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userFCMToken = snapshot.getValue(String::class.java)
                    if (!userFCMToken.isNullOrEmpty()) {
                        scheduleRemindersForUser(userFCMToken, reminderList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }

    private fun scheduleRemindersForUser(userFCMToken: String, reminderList: List<GeneralReminders>) {
        for (reminder in reminderList) {
            val reminderTime = reminder.schTime ?: continue
            val reminderDate = reminder.schDate ?: continue
            val reminderTitle = reminder.schTitle ?: continue

            // Construct notification message with reminder data
            val message = RemoteMessage.Builder(userFCMToken)
                .addData("title", reminderTitle)
                .addData("time", reminderTime)
                .addData("date", reminderDate)
                .build()

            FirebaseMessaging.getInstance().send(message)
        }
    }
}