package metospherus.app.modules

data class GeneralDocuments(
    val documentTitle: String? = null,
    val documentShortDescription: String? = null,
    val documentDate: String? = null,
    val documentSyncStatus: String? = null,
    val documentPreview: String? = null
) {
    constructor() : this( null, null, null, null, null)
}