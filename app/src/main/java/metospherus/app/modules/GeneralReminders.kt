package metospherus.app.modules

data class GeneralReminders(
    val schTime: String? = null,
    val schDate: String? = null,
    val schTitle: String? = null
) {
    constructor() : this("", "", "")
}