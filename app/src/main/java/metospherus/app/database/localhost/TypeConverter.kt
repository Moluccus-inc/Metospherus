package metospherus.app.database.localhost

import androidx.room.TypeConverter
import com.google.gson.Gson
import metospherus.app.database.profile_data.GeneralUserInformation

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
    fun fromMedicalProfessionals(value: GeneralUserInformation.MedicalProfessionals): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMedicalProfessionals(value: String): GeneralUserInformation.MedicalProfessionals {
        return gson.fromJson(value, GeneralUserInformation.MedicalProfessionals::class.java)
    }

    @TypeConverter
    fun fromGeneralDescription(value: GeneralUserInformation.GeneralDescription): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toGeneralDescription(value: String): GeneralUserInformation.GeneralDescription {
        return gson.fromJson(value, GeneralUserInformation.GeneralDescription::class.java)
    }

    @TypeConverter
    fun fromGeneralHealthInformation(value: GeneralUserInformation.GeneralHealthInformation): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toGeneralHealthInformation(value: String): GeneralUserInformation.GeneralHealthInformation {
        return gson.fromJson(value, GeneralUserInformation.GeneralHealthInformation::class.java)
    }

    @TypeConverter
    fun fromGeneralSystemInformation(value: GeneralUserInformation.GeneralSystemInformation): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toGeneralSystemInformation(value: String): GeneralUserInformation.GeneralSystemInformation {
        return gson.fromJson(value, GeneralUserInformation.GeneralSystemInformation::class.java)
    }

    @TypeConverter
    fun fromGeneralDatabaseInformation(value: GeneralUserInformation.GeneralDatabaseInformation): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toGeneralDatabaseInformation(value: String): GeneralUserInformation.GeneralDatabaseInformation {
        return gson.fromJson(value, GeneralUserInformation.GeneralDatabaseInformation::class.java)
    }

    @TypeConverter
    fun fromGeneralLegalInformation(value: GeneralUserInformation.GeneralLegalInformation): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toGeneralLegalInformation(value: String): GeneralUserInformation.GeneralLegalInformation {
        return gson.fromJson(value, GeneralUserInformation.GeneralLegalInformation::class.java)
    }
}

