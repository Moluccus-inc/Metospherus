package metospherus.app.database.localhost

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import metospherus.app.database.caching.MenstrualCyclesDao
import metospherus.app.database.caching.UserCompanionshipDao
import metospherus.app.database.caching.UserProfileDao
import metospherus.app.database.profile_data.GeneralUserInformation
import metospherus.app.modules.GeneralBrainResponse
import metospherus.app.modules.GeneralMenstrualCycle

@Database(entities = [GeneralUserInformation::class, GeneralMenstrualCycle::class, GeneralBrainResponse::class], version = 1)
@TypeConverters(TypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileLocal(): UserProfileDao
    abstract fun menstrualCycleLocal(): MenstrualCyclesDao
    abstract fun generalBrainResponse(): UserCompanionshipDao
    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "localhost"
                    ).fallbackToDestructiveMigration().build()
                }
            }
            return INSTANCE!!
        }
    }
}