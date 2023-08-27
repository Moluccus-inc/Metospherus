package metospherus.app.services

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.legacy.content.WakefulBroadcastReceiver

class WakefulAlertReceiver : WakefulBroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        // Delegate the work to the AlertReceiver
        AlertReceiver().onReceive(context, intent)
        completeWakefulIntent(intent)
    }
}
