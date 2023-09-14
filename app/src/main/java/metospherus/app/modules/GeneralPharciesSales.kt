package metospherus.app.modules

data class GeneralPharmaciesSales(
    val id: Long = 0,
    val name: String? = null,
    val description: String? = null,
    val currency: String? = null,
    val discount: String? = null,
    val generic_name: String? = null,
    val image: String? = null,
    val price: String? = null,
    var pharmacyUid: String? = null,
)
