package metospherus.app.modules

data class GeneralMedicalProfessions(
    val email: String? = null,
    val handle: String? = null,
    val name: String? = null,
    val medicalProfessionals: MedicalProfessionals,
    val uid: String? = null,
    val avatar: String? = null,
) {
    constructor() : this(
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
            null,
            null
        ),
        null,
        null
    )
}

data class MedicalProfessionals(
    val about: String? = null,
    val medicalProfessionType: String? = null,
    val mdGender: String? = null,
    val mdExperience: String? = null,
    val mdLicense: String? = null,
    val mdReference: String? = null,
    val mdReferenceEmail: String? = null,
    val mdRating: Float? = null,
) {
    constructor() : this(
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null)
}