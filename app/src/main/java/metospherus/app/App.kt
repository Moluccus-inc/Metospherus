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

/**
// Encrypt data before storing it in the database
val encryptedData = encryptionUtils.encrypt("sensitive_data")

// Decrypt data when you need to use it
val decryptedData = encryptionUtils.decrypt(encryptedData)
 **/