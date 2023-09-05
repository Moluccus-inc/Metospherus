package metospherus.app.database.localhost

import androidx.room.TypeConverter
import com.google.gson.Gson
import metospherus.app.database.profile_data.Profiles

class TypeConverter {
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

    @TypeConverter
    fun fromGeneralDescription(value: Profiles.GeneralDescription): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toGeneralDescription(value: String): Profiles.GeneralDescription {
        return gson.fromJson(value, Profiles.GeneralDescription::class.java)
    }

    @TypeConverter
    fun fromGeneralHealthInformation(value: Profiles.GeneralHealthInformation): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toGeneralHealthInformation(value: String): Profiles.GeneralHealthInformation {
        return gson.fromJson(value, Profiles.GeneralHealthInformation::class.java)
    }

    @TypeConverter
    fun fromGeneralSystemInformation(value: Profiles.GeneralSystemInformation): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toGeneralSystemInformation(value: String): Profiles.GeneralSystemInformation {
        return gson.fromJson(value, Profiles.GeneralSystemInformation::class.java)
    }

    @TypeConverter
    fun fromGeneralDatabaseInformation(value: Profiles.GeneralDatabaseInformation): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toGeneralDatabaseInformation(value: String): Profiles.GeneralDatabaseInformation {
        return gson.fromJson(value, Profiles.GeneralDatabaseInformation::class.java)
    }

    @TypeConverter
    fun fromGeneralLegalInformation(value: Profiles.GeneralLegalInformation): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toGeneralLegalInformation(value: String): Profiles.GeneralLegalInformation {
        return gson.fromJson(value, Profiles.GeneralLegalInformation::class.java)
    }
}

