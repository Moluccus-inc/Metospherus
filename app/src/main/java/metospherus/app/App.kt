package metospherus.app

import android.app.Application
import com.google.android.material.color.DynamicColors
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.getApps(this)
        FirebaseApp.initializeApp(applicationContext)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}