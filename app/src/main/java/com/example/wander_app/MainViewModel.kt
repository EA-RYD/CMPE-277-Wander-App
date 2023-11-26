package com.example.wander_app

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tutorial.chatgptapp.ChatGptRepository
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch


class MainViewModel : ViewModel() {
    val gpt = ChatGptRepository()
    val response = MutableLiveData<String>()
    val message = MutableLiveData<String>()
    val location = MutableLiveData<String>()
    val suggestionList = MutableLiveData<SuggestionList>()


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
