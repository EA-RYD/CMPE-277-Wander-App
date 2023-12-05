package com.example.wander_app

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.tutorial.chatgptapp.ChatGptRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope


class MainViewModel : ViewModel() {

//    val gpt = ChatGptRepository()
    val response = MutableLiveData<String>()
    val location = MutableLiveData<String>()
    val suggestionList = MutableLiveData<SuggestionList>()
    val rawSuggestionList = MutableLiveData<SuggestionList>()
    val taSearchResult = MutableLiveData<TASearchResult>()

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


//    fun sendRequest() {
//        gpt.callSendMessageApi()
//        gpt.callRunMessage()
//        viewModelScope.launch {
//            delay(20000) // Delay for 20 seconds (20000 milliseconds)
//            response.value = gpt.callRetrieveApi()
//        }
//    }

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

   fun updateImgUrlToSuggestion(id: Int, imgUrl:String) {
       Log.i(">>MainViewModel", "updateImgUrlToSuggestion: $id")
       val items = suggestionList.value?.suggestions?.toMutableList()
       val item = items?.get(id)
       if (item != null) {
           item.imgUrl = imgUrl
       }
         suggestionList.value = SuggestionList(items!!)
   }

    fun resetPhotoResult() {
        val initialPhotoItems = MutableList(6) { TAPhotoItem() }
        for (item in initialPhotoItems) {
            item.imgUrl = ""
            item.responseString = ""
            item.locationId = ""
        }
        taPhotoResult.postValue(initialPhotoItems)
    }
}

