package com.example.wander_app

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.tutorial.chatgptapp.ChatGptRepository
import kotlinx.coroutines.launch


class MainViewModel() : ViewModel() {

    val gpt = ChatGptRepository()
    val response = MutableLiveData<String>()
    val message = MutableLiveData<String>()
    val location = MutableLiveData<String>()
    val suggestionList = MutableLiveData<SuggestionList>()
    val taSearchResult = MutableLiveData<TASearchResult>()
    val loadedSuggestionList = MutableLiveData<MutableList<Suggestion>>()

    init {
        // Initialize with an empty TASearchResult
        taSearchResult.value = TASearchResult()
    }

    val taPhotoResult = MutableLiveData<MutableList<TAPhotoItem>>()

    init {
        // Initialize the MutableLiveData with a MutableList containing 6 TAPhotoItem instances
        val initialPhotoItems =
            MutableList(6) { TAPhotoItem() } // Replace TAPhotoItem() with appropriate constructor call if needed
        taPhotoResult.value = initialPhotoItems
    }

    val itinerary = MutableLiveData<MutableList<ItineraryItem>>()


    fun addSearchItem(taSearchItem: TASearchItem) {
        val currentResult = taSearchResult.value ?: TASearchResult()
        currentResult.searchItems.add(taSearchItem)
        taSearchResult.value = currentResult
    }

    fun setLocation(newLocation: String) {
        location.value = newLocation
    }


    fun sendRequest() {
        viewModelScope.launch {
            try {
                response.value = gpt.makeApiRequest()
                val gson = Gson()
                val gptResponse = gson.fromJson(response.value, GptResponse::class.java)
                Log.i(">>", "suggestions: ${gptResponse.choices[0].message.content}")

                val suggestions = gson.fromJson(
                    gptResponse.choices[0].message.content,
                    SuggestionList::class.java
                )
                updateAddressInSuggestions(suggestions)
                suggestionList.value = suggestions
                Log.i(">>", "suggestions: ${suggestionList.value}")

            } catch (e: Exception) {
                Log.e("MainViewModel", "Error in suspend function", e)
            }
        }
    }

    public fun updateMessage(newMessage: String) {
        gpt.addMessage(newMessage)
    }

    fun updatePhotoItemLocationId(suggestionId: Int, locationId: String) {
        val currentList = taPhotoResult.value ?: mutableListOf()

        if (suggestionId in currentList.indices) {
            val photoItem = currentList[suggestionId]
            photoItem.locationId = locationId

            // Update the LiveData
            taPhotoResult.value = currentList
        }
    }

    fun updatePhotoItemImgUrl(suggestionId: Int, imgUrl: String) {
        val currentList = taPhotoResult.value ?: mutableListOf()

        if (suggestionId in currentList.indices) {
            val photoItem = currentList[suggestionId]
            photoItem.imgUrl = imgUrl

            // Update the LiveData
            taPhotoResult.value = currentList
        }
    }

    fun updatePhotoItemResponseString(suggestionId: Int, responseString: String) {
        val currentList = taPhotoResult.value ?: mutableListOf()

        if (suggestionId in currentList.indices) {
            val photoItem = currentList[suggestionId]
            photoItem.responseString = responseString

            // Update the LiveData
            taPhotoResult.value = currentList
        }
    }


    fun transformItineraryToSuggestionList(itineraryItems: List<ItineraryItem>) {
        val suggestions = mutableListOf<Suggestion>()
        val reversedItineraryItems = itineraryItems.reversed()
        for (item in reversedItineraryItems) {
            suggestions.add(
                Suggestion(
                    item.imgUrl,
                    item.locationName,
                    "",
                    "",
                    "",
                    "",
                    item.description,
                    item.locationId,
                    item.responseString
                )
            )
        }
        loadedSuggestionList.postValue(suggestions.toMutableList())
    }

    fun transformItineraryToTaPhotoResult(itineraryItems: MutableList<ItineraryItem>) {
        val taPhotoItems = mutableListOf<TAPhotoItem>()
        val reversedItineraryItems = itineraryItems.reversed()
        for (item in reversedItineraryItems) {
            taPhotoItems.add(
                TAPhotoItem(
                    item.locationId,
                    item.responseString,
                    item.imgUrl
                )
            )
        }
        taPhotoResult.postValue(taPhotoItems.toMutableList())
    }

    fun addItemsToItinerary(newItems: List<ItineraryItem>) {
        val currentList = itinerary.value ?: mutableListOf()

        newItems.forEach { newItem ->
            if (currentList.none { it.locationName == newItem.locationName }) {
                currentList.add(newItem)
            }
        }

        itinerary.value = currentList
    }

    fun deleteItineraryItem(id: Int) {
        val currentList = itinerary.value ?: mutableListOf()
        currentList.removeAt(id)
        itinerary.value = currentList
    }

}

