package com.example.wander_app

import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide.init
import com.google.gson.Gson
import com.tutorial.chatgptapp.ChatGptRepository
import kotlinx.coroutines.launch
import java.io.UnsupportedEncodingException


class MainViewModel : ViewModel() {
    val gpt = ChatGptRepository()
    val response = MutableLiveData<String>()
    val message = MutableLiveData<String>()
    val location = MutableLiveData<String>()
    val suggestionList = MutableLiveData<SuggestionList>()
    val taSearchResult = MutableLiveData<TASearchResult>()
    val taPhotoResult = MutableLiveData<TAPhotoResult>()
    init {
        // Initialize with an empty TASearchResult
        taSearchResult.value = TASearchResult()
    }

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

                val suggestions = gson.fromJson(gptResponse.choices[0].message.content, SuggestionList::class.java)
                updateAddressInSuggestions(suggestions)
                suggestionList.value = suggestions
                Log.i(">>", "suggestions: ${suggestionList.value}")

            } catch (e: Exception) {
                Log.e("MainViewModel", "Error in suspend function", e)
            }
        }
    }
    public fun updateMessage(newMessage:String) {
        gpt.addMessage(newMessage)
    }

}
