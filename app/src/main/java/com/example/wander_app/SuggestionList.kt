package com.example.wander_app

data class SuggestionList(
    val suggestions: List<Suggestion>
)

data class Suggestion(
    val img:String,
    val name: String,
    val address: String,
    var _address: Address,
    val longitude: Double,
    val latitude: Double,
    val description: String,
    val locationId: String,
    val taApiResponse: String
)


data class Address(
    val street: String,
    val city: String,
    val state: String,
    val zip: String
)
fun updateAddressInSuggestions(suggestionList: SuggestionList) {
    suggestionList.suggestions.forEach { suggestion ->
        suggestion._address = parseAddress(suggestion.address)!!
    }
}

fun parseAddress(addressString: String): Address? {
    val parts = addressString.split(",").map { it.trim() }
    if (parts.size < 4) return null  // Not a valid address format

    val street = parts[0]
    val city = parts[1]
    val stateAndZip = parts[2].split(" ")

    // Check if state and ZIP are correctly formatted
    if (stateAndZip.size < 2) return null

    val state = stateAndZip[0]
    val zip = stateAndZip[1]

    return Address(street, city, state, zip)
}
