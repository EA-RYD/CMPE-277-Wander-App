package com.example.wander_app

data class SuggestionList(
    val suggestions: List<Suggestion>
)

data class Suggestion(
    var imgUrl: String,
    val name: String,
    val alias: String,
    val address: String,
    var streetAddress: String,
    val longitude: String,
    val latitude: String,
    val description: String,
    val locationId: String,
    val taApiResponse: String,
    var isChecked: Boolean = false,
    var btnEnabled: Boolean = true

){

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

    // Split the first part of the address into words
    val streetPartWords = parts[0].split(" ")

    // Check if the first word is a number
    val regex = Regex("^[0-9]+\$")
    val firstWordIsNumber = streetPartWords.firstOrNull()?.matches(regex) == true

    // If the first word is a number, drop it; otherwise, keep the address as is
    return if (firstWordIsNumber) {
        streetPartWords.drop(1).joinToString(" ")
    } else {
        parts[0]
    }
}