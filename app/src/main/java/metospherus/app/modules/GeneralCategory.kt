package metospherus.app.modules

data class GeneralCategory(val titleCategory: String, val imageCategory: Int, val enabled: Boolean) {
    constructor() : this("", 0, false)
}