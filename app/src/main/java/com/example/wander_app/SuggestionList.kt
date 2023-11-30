package com.example.wander_app

data class SuggestionList(
    val suggestions: List<Suggestion>
)

data class Suggestion(
    var img:String,
    val name: String,
    val address: String,
    var streetAddress: String,
    val longitude: String,
    val latitude: String,
    val description: String,
    val locationId: String,
    val taApiResponse: String,
    var isChecked: Boolean = false

){
    fun setImgUrl(imgUrl: String) {
        this.img = imgUrl
    }
    fun getChecked(): Boolean {
        return isChecked
    }

}


fun updateAddressInSuggestions(suggestions:SuggestionList){
    for (suggestion in suggestions.suggestions) {
        suggestion.streetAddress = parseAddress(suggestion.address)
    }
}

fun parseAddress(addressString: String): String {
    val parts = addressString.split(",").map { it.trim() }
    if (parts.isEmpty()) return ""  // Not a valid address format
    return parts[0]
}