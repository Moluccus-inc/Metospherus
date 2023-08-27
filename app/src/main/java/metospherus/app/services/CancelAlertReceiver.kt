package metospherus.app.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Vibrator

class CancelAlertReceiver : BroadcastReceiver() {

    companion object {
        var vibrator: Vibrator? = null
        var mediaPlayer: MediaPlayer? = null
    }

    override fun onReceive(context: Context, intent: Intent) {
        // Cancel the ongoing vibration and sound
        vibrator?.cancel()
        mediaPlayer?.stop()
        mediaPlayer?.release()
    }
}
