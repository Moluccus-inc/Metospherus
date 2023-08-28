package metospherus.app.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import metospherus.app.R

@Suppress("DEPRECATION")
class AlertReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title")
        val message = intent.getStringExtra("message")

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create an action to cancel the alert
        val cancelIntent = Intent(context, CancelAlertReceiver::class.java)
        val cancelPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create the main notification with cancel action
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.metospherus)
            .setAutoCancel(true)
            .addAction(android.R.drawable.ic_delete, "Cancel", cancelPendingIntent)
            .build()

        notificationManager.notify(0, notification)

        // Vibrate for 1 minute
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            val pattern = longArrayOf(0, 1000)
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
        }

        // Play alarm sound for 1 minute
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val mediaPlayer = MediaPlayer.create(context, alarmUri)
        mediaPlayer.isLooping = true
        mediaPlayer.start()

        // Save the vibrator and media player references in CancelAlertReceiver
        CancelAlertReceiver.vibrator = vibrator
        CancelAlertReceiver.mediaPlayer = mediaPlayer
    }

    companion object {
        private const val CHANNEL_ID = "alert_channel"
    }
}