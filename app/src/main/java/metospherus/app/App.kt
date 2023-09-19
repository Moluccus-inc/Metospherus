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
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.internal.concurrent.TaskRunner

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
                val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
                val configSettings = remoteConfigSettings {
                    minimumFetchIntervalInSeconds = 3600
                }
                remoteConfig.setConfigSettingsAsync(configSettings)

                val alreadyConfinedKeys = sharedPreferences.getString("api_key", "").toString()
                if (alreadyConfinedKeys.isNotEmpty()) {
                    remoteConfig.fetchAndActivate()
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val apiKey = remoteConfig.getString("APIKEY")
                                if (alreadyConfinedKeys == apiKey) {
                                    // do nothing
                                } else {
                                    if (apiKey != null) {
                                        sharedPreferences.edit().putString("api_key", apiKey).apply()
                                    }
                                }
                            }
                        }
                } else {
                    remoteConfig.fetchAndActivate()
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val apiKey = remoteConfig.getString("APIKEY")
                                if (apiKey != null) {
                                    sharedPreferences.edit().putString("api_key", apiKey).apply()
                                }
                            }
                        }
                }

                val appVer = sharedPreferences.getString("version", "")!!
                if(appVer.isEmpty() || appVer != BuildConfig.VERSION_NAME){
                    sharedPreferences.edit(commit = true){
                        putString("version", BuildConfig.VERSION_NAME)
                    }
                }
            } catch (e: Exception){
                Looper.prepare().runCatching {
                    Toast.makeText(instance, e.message, Toast.LENGTH_SHORT).show()
                }
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val TAG = "App"
        lateinit var applicationScope: CoroutineScope
        lateinit var instance: App
    }
}