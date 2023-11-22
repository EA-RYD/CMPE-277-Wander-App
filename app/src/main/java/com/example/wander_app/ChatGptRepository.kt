package com.tutorial.chatgptapp
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


data class ChatMessage(val role: String, val content: String)
data class ChatSession(val model: String, val messages: List<ChatMessage>)
class ChatGptRepository() {
    val apiKey: String
    val urlEndPoint: String
    val session: MutableList<ChatMessage>

    init {

        apiKey = ""

        urlEndPoint = "https://api.openai.com/v1/chat/completions"
        val jsonString =
            """
                {
                    "model": "gpt-3.5-turbo",
                    "messages": [
                        {
                            "role": "system",
                            "content": "You are working as a module in an application to provide travel suggestions for the users. Your response should always in JSON format."
                        },
                        {
                            "role": "system",
                            "content": "Your Json respond should include exact 6 suggestions, and for each suggestion always include name, address, and longitude and latitude of the place and a short description(no more than 20 words)."
                        },
                        {
                            "role": "system",
                            "content": "User can provide a location or a type of activity."
                        },
                        {
                            "role": "system",
                            "content": "You can update the suggestion list, but stay with JSON format defined."
                        }
                    ]
                }
            """.trimIndent()

        // Parse the JSON string
        val gson = Gson()
        val chatSession = gson.fromJson(jsonString, ChatSession::class.java)
        session = chatSession.messages.toMutableList()
        Log.i(">>", session.toString())

    }

    fun addMessage(message: String) {
        val newMessage = ChatMessage("user", message)
        session.add(newMessage)
        Log.i(">>addMessage", session.toString())
    }
    suspend fun makeApiRequest(): String {
        return withContext(Dispatchers.IO) {
            try {
                Log.i(">>", "Sending request")
                val client = OkHttpClient.Builder()
                    .callTimeout(3, java.util.concurrent.TimeUnit.MINUTES)
                    .readTimeout(3, java.util.concurrent.TimeUnit.MINUTES)
                    .writeTimeout(3, java.util.concurrent.TimeUnit.MINUTES)
                    .build()
                val request = Request.Builder()
                    .url(urlEndPoint)
                    .header("Authorization", "Bearer $apiKey")
                    .post(createRequestBody2(session))
                    .build()
                Log.i(">>", "Waiting for chatGPT's response, it may take a while...")
                val response = client.newCall(request).execute()
                try {
                    if (response.isSuccessful) {
                        val responseBodyString = response.body?.string()
                        Log.i(">>", "Response: $responseBodyString")

                        responseBodyString ?: "Empty response"
                    } else {
                        "Request failed with code: ${response.code}"
                    }
                } finally {
                    response.close()
                }
            } catch (e: IOException) {
                Log.e("ChatGptRepository", "Network error: ${e.message}")
                "Network error: ${e.message}"
            }
        }
    }


    private fun createRequestBody2(session:MutableList<ChatMessage>): okhttp3.RequestBody {
        val messagesArray = session.map { chatMessage ->
            mapOf(
                "role" to chatMessage.role,
                "content" to chatMessage.content
            )
        }

        val json = mapOf(
            "model" to "gpt-3.5-turbo",
            "messages" to messagesArray
        )

        val jsonString = Gson().toJson(json)
        Log.i("ChatGptRepository", "Request body: $jsonString")
        return jsonString.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

}
