package metospherus.app.modules

data class GeneralPills(
    var pushkey: String? = null,
    val medicineName: String? = null,
    val medicineAvatar: String? = null,
    val medicineAmount: String? = null,
    val medicineTime: String? = null,
    val medicineQuantity: String? = null,
    val medicineDate: String? = null
) {
    constructor() : this("", "", "", "", "", "")
}
