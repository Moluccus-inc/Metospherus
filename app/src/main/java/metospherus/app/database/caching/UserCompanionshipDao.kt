package metospherus.app.database.caching

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import metospherus.app.modules.GeneralBrainResponse

@Dao
interface UserCompanionshipDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserCompanionShip(userCompanionShip: GeneralBrainResponse)

    @Query("SELECT * FROM USER_COMPANIONSHIP LIMIT 1")
    suspend fun getUserCompanionShip(): GeneralBrainResponse?

    @Query("DELETE FROM USER_COMPANIONSHIP")
    fun deleteAllUserData()
}