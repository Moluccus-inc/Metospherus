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
    val country: String? = null,

    val quotations: QuotationsAndPrices? = null,
    val provisioning: ProvisionServices? = null,
    val paymentMethods: PaymentMethods? = null,
    val medicineIdentifiers: MedicineIdentifiers? = null,
) {
    data class QuotationsAndPrices(
        val unit: String? = null, // per strip , tablet etc
        val price: String? = null,
        val currency: String? = null,
        val quantityUnit: Int? = null,
        val qualityUnit: Int? = null
    )
    data class ProvisionServices(
        val shipping: Boolean? = false,
        val localDelivery: Boolean? = false,
    )
    data class PaymentMethods(
        val cashOnDelivery: Boolean? = false,
        val cashless: Boolean? = false,
    )
    data class MedicineIdentifiers(
        val brandName: String? = null,
        val genericName: String? = null,
        val otherNames: String? = null,
        val description: String? = null,
        val medicalUsages: String? = null,
        val disclaimer: String? = null,
    )
}
