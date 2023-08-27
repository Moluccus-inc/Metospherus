package metospherus.app.database.localhost

import androidx.room.TypeConverter
import com.google.gson.Gson

class AvatarTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromAvatar(avatar: Any?): String? {
        return avatar as? String
    }

    @TypeConverter
    fun toAvatar(avatarString: String?): Any? {
        return avatarString
    }
   /** @TypeConverter
    fun fromPatientDetails(patientDetails: PatientDetails?): String? {
        return gson.toJson(patientDetails)
    }

    @TypeConverter
    fun toPatientDetails(patientDetailsString: String?): PatientDetails? {
        if (patientDetailsString.isNullOrEmpty()) {
            return null
        }
        val type = object : TypeToken<PatientDetails>() {}.type
        return gson.fromJson(patientDetailsString, type)
    } **/
}

