package metospherus.app.database.profile_data

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "USER_PROFILE")
data class GeneralUserInformation(
    @PrimaryKey val id: Long = 0, // Provide a default value for id
    val accountType: String? = null,
    val email: String? = null,
    val handle: String? = null,
    val uid: String? = null,
    val userId: String? = null,
    val avatar: String? = null,
    val phoneNumber: String? = null,
    val generalDescription: GeneralDescription = GeneralDescription(),
    val generalHealthInformation: GeneralHealthInformation = GeneralHealthInformation(),
    val generalSystemInformation: GeneralSystemInformation = GeneralSystemInformation(),
    val generalDatabaseInformation: GeneralDatabaseInformation = GeneralDatabaseInformation(),
    val generalLegalInformation: GeneralLegalInformation = GeneralLegalInformation(),
    val medicalProfessionals: MedicalProfessionals = MedicalProfessionals()
) {
    data class MedicalProfessionals(
        val about: String? = null,
        val medicalProfessionType: String? = null,
        val mdGender: String? = null,
        val mdExperience: String? = null,
        val mdLicense: String? = null,
        val mdReference: String? = null,
        val mdReferenceEmail: String? = null
    )
    data class GeneralDescription(
        val usrFullLegalName: String? = null,
        val usrPreferedName: String? = null,
        val usrPrimaryEmail: String? = null,
        val usrPrimaryPhone: String? = null,
        val usrDateOfBirth: String? = null,
        val usrDistinguishedHandle: String? = null,
        val physicalAddress: String?= null,
        val countryLocation: String? = null,
    )
    data class GeneralHealthInformation(
        val heightRecord: String? = null,
        val weightRecord: String? = null,
        val bloodGroupRecord: String? = null,
        val allergiesRecord: String? = null,
        val genderIdRecord: String? = null,
        val familyIdRecord: String? = null,
        val heartPulseRecord: String? = null,
        val temperatureRecord: String? = null,
        val sleepTimeRecord: String? = null,
        val pillsAvailableRecord: String? = null,
        val stepsRecord: String? = null,
        val timeTakenRecord: String? = null,
        val deviceUsageTimeRecord: String? = null,
        val moodsRecord: MoodsRecordInfo = MoodsRecordInfo()
    )
    data class MoodsRecordInfo(
        val monday: Int? = 5,
        val tuesday: Int? = 5,
        val wednesday: Int? = 5,
        val thursday: Int? = 5,
        val friday: Int? = 5,
        val saturday: Int? = 5,
        val sunday: Int? = 5
    )
    data class GeneralSystemInformation(
        val activeTimeStatus: String?= null,
        val activeLogStatus: Boolean?= false,
    )
    data class GeneralDatabaseInformation(
        val applicationVersion: String?= null,
        val applicationDatabaseVersion: String?= null,
        val userUniqueIdentificationNumber: String?= null,
        val userGeneralIdentificationNumber: String?= null
    )
    data class GeneralLegalInformation(
        val usrGeneralApplicationUsageConsent: Boolean? = false,
        val usrGeneralInformationUsageConsent: Boolean? = false,
        val usrHealthDataConsent: Boolean? = false,
        val usrWorkoutDataConsent: Boolean? = false,
        val usrDietaryDataConsent: Boolean? = false,
        val usrMedicalRecordsConsent: Boolean? = false,
        val usrLocationDataConsent: Boolean? = false,
        val usrCommunicationConsent: Boolean? = false,
        val usrPrivacyConsent: Boolean? = false,
        val usrLegalUseCaseDataConsent: Boolean? = false,
    )
}
