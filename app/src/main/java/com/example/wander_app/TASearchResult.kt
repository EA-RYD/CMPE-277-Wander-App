package com.example.wander_app

class TASearchItem(private val suggestionId: String, private var locationID: String) {
    fun setLocationID(newLocationID: String) {
        locationID = newLocationID
    }

    fun getSuggestionId(): String? {
        return suggestionId
    }

    fun getLocationId(): String? {
        return locationID
    }
    override fun toString(): String {
        return "TASearchItem{" +
                "suggestionId='" + suggestionId + '\'' +
                ", locationID='" + locationID + '\'' +
                '}'
    }
}
class TASearchResult {
    val searchItems: MutableList<TASearchItem> = mutableListOf()
    override fun toString(): String {
        val itemsString = searchItems.joinToString(separator = ", ", prefix = "[", postfix = "]") { it.toString() }
        return "TASearchResult{searchItems=$itemsString}"
    }
}
