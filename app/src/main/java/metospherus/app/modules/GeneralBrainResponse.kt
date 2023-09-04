package metospherus.app.modules

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "USER_COMPANIONSHIP")
data class GeneralBrainResponse(
    @PrimaryKey val id: Long,
    val questionAsked: String,
    val responsesGiven: String
)