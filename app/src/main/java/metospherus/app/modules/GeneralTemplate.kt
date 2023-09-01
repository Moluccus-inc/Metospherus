package metospherus.app.modules

data class GeneralTemplate(
    val name: String? = null,
    val img: String? = null,
    val selected: Boolean = false,
    var pushKey: String? = null,
) {
    constructor() : this("", "", false, "")
}