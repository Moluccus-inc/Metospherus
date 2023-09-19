package metospherus.app.database.caching

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import metospherus.app.database.profile_data.GeneralUserInformation

@Dao
interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserPatient(userProfile: GeneralUserInformation)

    @Query("SELECT * FROM USER_PROFILE LIMIT 1")
    suspend fun getUserPatient(): GeneralUserInformation?

    @Query("DELETE FROM USER_PROFILE")
    fun deleteAllUserData()
}