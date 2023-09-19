package metospherus.app.modules

data class GeneralDocuments(
    val time: String? = null,
    val formattedText: String? = null,
    val formattingInfoList: String? = null
) {
    constructor() : this( null, null, null)
}