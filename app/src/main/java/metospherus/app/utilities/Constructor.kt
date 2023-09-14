package metospherus.app.utilities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import metospherus.app.database.localhost.AppDatabase
import metospherus.app.database.profile_data.Profiles
import metospherus.app.modules.GeneralBrainResponse
import metospherus.app.modules.GeneralMenstrualCycle
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random

object Constructor {
    const val METOSPHERUS_API = "https://metospherus.vercel.app/api"
    const val CHANNEL_ID = "channel_id"
    fun View.show() {
        visibility = View.VISIBLE
    }

    fun View.hide() {
        visibility = View.GONE
    }

    fun postDelayed(delayMillis: Long, task: () -> Unit) {
        Handler(Looper.myLooper()!!).postDelayed(task, delayMillis)
    }

    suspend fun insertOrUpdateUserProfile(userProfile: Profiles, appDatabase: AppDatabase) {
        withContext(Dispatchers.IO) {
            appDatabase.profileLocal().insertOrUpdateUserPatient(userProfile)
        }
    }
    suspend fun getUserProfilesFromDatabase(appDatabase: AppDatabase): Profiles? {
        return appDatabase.profileLocal().getUserPatient()
    }
    suspend fun getMenstrualCyclesFromLocalDatabase(appDatabase: AppDatabase): GeneralMenstrualCycle? {
        return appDatabase.menstrualCycleLocal().getMenstrualCycles()
    }
    suspend fun insertOrUpdateMenstrualCycles(menstrual: GeneralMenstrualCycle, appDatabase: AppDatabase) {
        withContext(Dispatchers.IO) {
            appDatabase.menstrualCycleLocal().insertOrUpdateMenstrualCycles(menstrual)
        }
    }
    suspend fun insertOrUpdateUserCompanionShip(userCompanionship: GeneralBrainResponse, appDatabase: AppDatabase) {
        withContext(Dispatchers.IO) {
            appDatabase.generalBrainResponse().insertOrUpdateUserCompanionShip(userCompanionship)
        }
    }
    suspend fun getCompanionShipFromLocalDatabase(appDatabase: AppDatabase): GeneralBrainResponse? {
        return appDatabase.generalBrainResponse().getUserCompanionShip()
    }

    fun checkLocationPermission(context: Context): Boolean {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val granted = PackageManager.PERMISSION_GRANTED
        return ContextCompat.checkSelfPermission(context, permission) == granted
    }

    // Function to get the user's country based on their coordinates
    private fun getUserCountry(context: Context, latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(context)
        val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
        return addresses?.firstOrNull()?.countryName
    }

    // Function to get the approximate location (e.g., "Uganda", "Kenya") based on coordinates
    fun getApproximateLocation(context: Context, latitude: Double, longitude: Double): String? {
        val country = getUserCountry(context, latitude, longitude)
        return country?.takeIf { it.isNotEmpty() }
    }

    // Function to get the user's live location using the LocationManager
    /**
     * Get the user's location using the LocationManager
     * @param getLiveLocation
     * val liveLocation = LocationUtils.getLiveLocation(context)
     * if (liveLocation != null) {
     *     // Now you have the live location (latitude and longitude) to work with
     * }
     */
    @SuppressLint("MissingPermission")
    fun getLiveLocation(context: Context): Any? {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = android.location.Criteria()
        criteria.accuracy = android.location.Criteria.ACCURACY_FINE
        val provider = locationManager.getBestProvider(criteria, true)
        return provider?.let {
            locationManager.getLastKnownLocation(it)
        }
    }
    // val approximateLocation = LocationUtils.getApproximateLocation(context, latitude, longitude)
    // Now you can use approximateLocation for your purposes
    // val userCountry = LocationUtils.getUserCountry(context, latitude, longitude)
    // Now you can use userCountry for your purposes
    // val liveLocation = LocationUtils.getLiveLocation(context)
    // if (liveLocation != null) {
        // Now you have the live location (latitude and longitude) to work with
    // }



    // Function to get app usage statistics for a specific time range
    private fun getAppUsageStats(
        context: Context,
        startTime: Long,
        endTime: Long
    ): List<UsageStats> {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        return usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )
    }

    // Function to get daily app usage statistics
    fun getDailyAppUsageStats(context: Context): List<UsageStats> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        return getAppUsageStats(context, startTime, endTime)
    }

    // Function to get weekly app usage statistics
    fun getWeeklyAppUsageStats(context: Context): List<UsageStats> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7) // 7 days ago
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        return getAppUsageStats(context, startTime, endTime)
    }

    /**
     * Function to get monthly app usage statistics
     * @param context
     */
    fun getMonthlyAppUsageStats(context: Context): List<UsageStats> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1) // 1 month ago
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        return getAppUsageStats(context, startTime, endTime)
    }
    // val dailyUsageStats = AppUsageTracker.getDailyAppUsageStats(context)
    // val weeklyUsageStats = AppUsageTracker.getWeeklyAppUsageStats(context)
    // val monthlyUsageStats = AppUsageTracker.getMonthlyAppUsageStats(context)
    fun dpToPx(dp: Int): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dp * scale).toInt()
    }
}