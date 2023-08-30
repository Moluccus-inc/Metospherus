package metospherus.app.modules

data class GeneralSearchResults(val searchAuthor: String, val searchResponse: String) {
    constructor() : this("", "")
}