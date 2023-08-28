package metospherus.app.modules

data class GeneralCategory(val titleCategory: String, val imageCategory: String, val enabled: Boolean) {
    constructor() : this("", "", false)
}