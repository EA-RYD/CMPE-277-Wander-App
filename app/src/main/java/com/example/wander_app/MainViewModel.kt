package com.example.wander_app

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tutorial.chatgptapp.ChatGptRepository
import com.tutorial.chatgptapp.ChatMessage
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class MainViewModel : ViewModel() {
    val gpt = ChatGptRepository()
    val response = MutableLiveData<String>()
    val message = MutableLiveData<String>()


    fun getChatResponse(): MutableLiveData<String> {
        return response
    }


    fun updateMessage(newMessage: String) {
        Log.i(">>", "new message: $newMessage")
        viewModelScope.launch {
            try {
                gpt.addMessage(newMessage)
                response.value = gpt.makeApiRequest()

            } catch (e: Exception) {
                Log.e("MainViewModel", "Error in suspend function", e)
            }
        }
    }
}
