package metospherus.app.database.profile_data

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "USER_PROFILE")
data class Profiles(
    @PrimaryKey val id: Long,
    val accountType: String? = null,
    val email: String? = null,
    val handle: String? = null,
    val name: String? = null,
    val uid: String? = null,
    val userId: String? = null,
    val address: String? = null,
    val gender: String? = null,
    val avatar: String? = null,
    val legalName: String? = null,
    val phoneNumber: String? = null,
    val active_time: String? = null,
    val active_status: Boolean? = false,
    val dob: String? = null,
    val allergies: String? = null,
    val blood_group: String? = null,
    val height: String? = null,
    val weight: String? = null,
    val medicalProfessionals: MedicalProfessionals,
) {

    data class MedicalProfessionals(
        val about: String? = null,
        val medicalProfessionType: String? = null,
        val mdGender: String? = null,
        val mdExperience: String? = null,
        val mdLicense: String? = null,
        val mdReference: String? = null,
        val mdReferenceEmail: String? = null,
    ) {
        constructor() : this(
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
    }

    constructor() : this(
        0,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        false,
        null,
        null,
        null,
        null,
        null,
        MedicalProfessionals(
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
    )
}