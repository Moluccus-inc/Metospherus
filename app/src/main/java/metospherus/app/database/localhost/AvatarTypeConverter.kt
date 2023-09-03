package metospherus.app.database.localhost

import androidx.room.TypeConverter
import com.google.gson.Gson
import metospherus.app.database.profile_data.Profiles

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

    @TypeConverter
    fun fromMedicalProfessionals(value: Profiles.MedicalProfessionals): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMedicalProfessionals(value: String): Profiles.MedicalProfessionals {
        return gson.fromJson(value, Profiles.MedicalProfessionals::class.java)
    }
}

