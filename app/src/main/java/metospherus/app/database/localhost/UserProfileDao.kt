package metospherus.app.database.localhost

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import metospherus.app.database.profile_data.Profiles

@Dao
interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserPatient(userProfile: Profiles)

    @Query("SELECT * FROM USER_PROFILE LIMIT 1")
    suspend fun getUserPatient(): Profiles?
}