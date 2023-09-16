package metospherus.app.database.localhost

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import metospherus.app.database.profile_data.Profiles
import metospherus.app.modules.GeneralMenstrualCycle

@Dao
interface MenstrualCyclesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMenstrualCycles(menstrualCycle: GeneralMenstrualCycle)

    @Query("SELECT * FROM MENSTRUAL_CYCLES LIMIT 1")
    suspend fun getMenstrualCycles(): GeneralMenstrualCycle?

    @Query("DELETE FROM MENSTRUAL_CYCLES")
    fun deleteAllUserData()
}