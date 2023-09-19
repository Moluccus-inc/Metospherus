package metospherus.app.services

import android.annotation.SuppressLint
import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import metospherus.app.App
import metospherus.app.database.profile_data.GeneralUserInformation
import metospherus.app.modules.FCMNotification
import metospherus.app.modules.FCMNotificationData
import metospherus.app.modules.FCMResponse
import metospherus.app.modules.RetrofitClient
import metospherus.app.utilities.Constructor.ACTION_REPLY
import metospherus.app.utilities.Constructor.KEY_REPLY
import metospherus.app.utilities.FirebaseConfig
import metospherus.app.utilities.MoluccusToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar

class NotificationReceiver : BroadcastReceiver() {

    private lateinit var db : FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    @SuppressLint("SimpleDateFormat")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ACTION_REPLY) {
            val remoteInput = RemoteInput.getResultsFromIntent(intent)
            if (remoteInput != null) {
                auth = Firebase.auth
                db = FirebaseDatabase.getInstance()
                val message = remoteInput.getCharSequence(KEY_REPLY)?.toString()
                val otheruseruid = remoteInput.getCharSequence("")?.toString()

                val calendar = Calendar.getInstance()
                val timeFormat = SimpleDateFormat("h:mm:ss a")
                val formattedTime = timeFormat.format(calendar.time)

                val dateFormat = SimpleDateFormat("MMMM d, yyyy")
                val formattedDate = dateFormat.format(calendar.time)

                val messageSender = mapOf(
                    "message" to message,
                    "senderUid" to auth.currentUser?.uid,
                    "timestamp" to formattedTime.toString(),
                    "datestamp" to formattedDate.toString(),
                    "viewed" to "✔"
                )

                val messageKey = if (auth.currentUser?.uid!! < otheruseruid.toString()) {
                    "${auth.currentUser?.uid}•${otheruseruid}"
                } else {
                    "${otheruseruid}•${auth.currentUser?.uid}"
                }

                val messageReference = db.getReference("MedicalMessenger").child(messageKey)
                val newMessageReference = messageReference.push()
                newMessageReference.setValue(messageSender)
                    .addOnSuccessListener {
                        sendNotificationToOtherUser(otheruseruid, message)
                    }
                    .addOnFailureListener {

                    }
            }
        }
    }

    private fun sendNotificationToOtherUser(otherUserUid: String?, messageBody: String?) {
        val fcmService = RetrofitClient.getFCMService()
        val headers = mapOf(
            "Content-Type" to "application/json",
            "Authorization" to "key=AAAAnB858Xs:APA91bG1VtuWm1hZ8VDE85jy9pdaWiHG_XIXJ8ISqQPN_TRd0bpLI72bqpDgYtufEsEEvUjigNsr4wUPGRtNZz2JZQ1MY0g2G1XxW2o454ttGwxOx09Xemq_KxgE0ixGHCTVrE8KB4Ch"
        )
        FirebaseConfig.retrieveRealtimeDatabaseOnListener(
            db,
            "participants/${auth.currentUser?.uid}",
            App.instance
        ) { dataSnapshot ->
            val avatarValue = dataSnapshot.getValue(GeneralUserInformation::class.java)
            FirebaseConfig.retrieveRealtimeDatabaseOnListener(
                db,
                "MedicalMessenger/FcmTokens",
                App.instance
            ) { dataKeySnap ->
                if (avatarValue != null) {
                    val notification = FCMNotification(
                        to = dataKeySnap.child(otherUserUid.toString()).getValue(String::class.java),
                        data = FCMNotificationData(
                            title = avatarValue.generalDescription.usrPreferedName,
                            body = messageBody,
                            imageUrl = avatarValue.avatar,
                            otherUserUid = otherUserUid
                        )
                    )

                    val call = fcmService.sendNotification(headers, notification)
                    call.enqueue(object : Callback<FCMResponse> {
                        override fun onResponse(
                            call: Call<FCMResponse>,
                            response: Response<FCMResponse>
                        ) {
                            if (response.isSuccessful) {
                                MoluccusToast(App.instance).showSuccess("Message Sent")
                            } else {
                                MoluccusToast(App.instance).showError("Error message not sent")
                            }
                        }

                        override fun onFailure(call: Call<FCMResponse>, t: Throwable) {}
                    })
                }
            }
        }
    }
}
