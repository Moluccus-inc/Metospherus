package metospherus.app.modules

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "MENSTRUAL_CYCLES")
data class GeneralMenstrualCycle(
    @PrimaryKey val id: Long,
    val previous_start_date: String? = null,
    val previous_end_date: String? = null,
    val cycle_length: String?= null,
    val longest_cycle: String? = null,
    val notes: String? = null
) {
    constructor() : this(0,"", "", "", "", "")
}
