package metospherus.app

import android.app.Application
import android.os.Looper
import android.widget.Toast
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.android.material.color.DynamicColors
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.ktx.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import metospherus.app.update.UpdateUtil
import metospherus.app.utilities.MoluccusToast

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this

        Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance(),
        )

        FirebaseApp.getApps(this)
        FirebaseApp.initializeApp(applicationContext)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        DynamicColors.applyToActivitiesIfAvailable(this)

        val sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(this)
        applicationScope = CoroutineScope(SupervisorJob())
        applicationScope.launch((Dispatchers.IO)) {
            try {
                val appVer = sharedPreferences.getString("version", "")!!
                if(appVer.isEmpty() || appVer != BuildConfig.VERSION_NAME){
                    sharedPreferences.edit(commit = true){
                        putString("version", BuildConfig.VERSION_NAME)
                    }
                }
            } catch (e: Exception){
                Looper.prepare().runCatching {
                    Toast.makeText(this@App, e.message, Toast.LENGTH_SHORT).show()
                }
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val TAG = "App"
        private lateinit var applicationScope: CoroutineScope
        lateinit var instance: App
    }
}