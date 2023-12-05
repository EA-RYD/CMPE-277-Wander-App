package com.tutorial.chatgptapp
import android.util.Log
import com.bumptech.glide.Glide.init
import com.example.wander_app.Suggestion
import com.example.wander_app.SuggestionList
import com.example.wander_app.parseAddress
import com.example.wander_app.updateAddressInSuggestions
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


data class ChatMessage(val role: String, val content: String)
data class ChatSession(val model: String, val messages: List<ChatMessage>)
class ChatGptRepository() {
    val apiKey: String
    val chatGptTreadId: String
    val chatGptAssistantId: String
    val sendMessageStamp: Long
    val runCreatedStamp: Long
    val retrieveCreatedStamp: Long

    init {
        apiKey = ""
        chatGptTreadId = "thread_BuWBNvVQcyjttKpEw3nv01Vm"
        chatGptAssistantId = "asst_YcBbfR62vg3WyymuxF8Epd6m"
        sendMessageStamp = 0
        runCreatedStamp = 0
        retrieveCreatedStamp = 0
    }


    fun callSendMessageApi(message:String) {
        val client = OkHttpClient()

        val json = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body = RequestBody.create(json, """
        {
            "role": "user",
            "content": "$message"
        }
    """.trimIndent())
        val request = Request.Builder()
            .url("https://api.openai.com/v1/threads/$chatGptTreadId/messages")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("OpenAI-Beta", "assistants=v1")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(">>callSendMessageApi", "API call failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i(">>callSendMessageApi", "Response: ${response.body?.string()}")
            }
        })
    }

    fun callRunMessage() {
        val client = OkHttpClient()

        val jsonMediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = RequestBody.create(jsonMediaType, """
        {
            "assistant_id": "$chatGptAssistantId"
        }
        """.trimIndent())

        val request = Request.Builder()
            .url("https://api.openai.com/v1/threads/$chatGptTreadId/runs")
            .post(requestBody)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .addHeader("OpenAI-Beta", "assistants=v1")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle the failure case
                Log.e(">>callRunMessage", "API call failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle the successful response
                Log.i(">>callRunMessage", "Response: ${response.body?.string()}")
            }
        })
    }
    fun callRetrieveApi() {
        val client = OkHttpClient()

        // Define a recursive function for repeated calls
        fun makeApiCall() {
            val request = Request.Builder()
                .url("https://api.openai.com/v1/threads/$chatGptTreadId/messages")
                .get()  // Since we're retrieving data, we use GET
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("OpenAI-Beta", "assistants=v1")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // Log the error
                    Log.e(">>callRetrieveApi", "API call failed", e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        response.body?.string()?.let { responseData ->
                            try {
                                val jsonObject = JSONObject(responseData)
                                val dataArray = jsonObject.getJSONArray("data")
                                if (dataArray.length() > 0) {
                                    val firstMessageObject = dataArray.getJSONObject(0)
                                    val role = firstMessageObject.getString("role")

                                    val contentArray = firstMessageObject.getJSONArray("content")
                                    if (contentArray.length() > 0) {
                                        val firstContentObject = contentArray.getJSONObject(0)
                                        val textObject = firstContentObject.getJSONObject("text")
                                        val value = textObject.getString("value")

                                        CoroutineScope(Dispatchers.Main).launch {
                                            if (value.isNotEmpty()) {
                                                Log.i(">>callRetrieveApi", "Value: $value")

                                                // put suggestion to the raw suggestion list
                                                val suggestions = convertJsonToSuggestionList(value)
                                                Log.i(">>callRetrieveApi", "Suggestion: $suggestions")
                                            } else {
                                                // Wait for 5 seconds before making another call
                                                delay(5000)
                                                makeApiCall()
                                            }
                                        }
                                    } else {

                                    }

                                } else { }
                            } catch (e: JSONException) {
                                Log.e(">>callRetrieveApi", "JSON parsing error", e)
                            }
                        }
                    } else {
                        Log.e(">>callRetrieveApi", "API call was not successful, Response JSON: ${response.body?.string()}")
                    }
                }
            })
        }

        // Initial API call
        makeApiCall()
    }


    private fun parseMessageList(jsonString: String): ApiResponse {
        val gson = Gson()
        return gson.fromJson(jsonString, ApiResponse::class.java)
    }

    data class ApiResponse(
        val _object: String,
        val data: List<Message>
    )
    data class Message(
        val id: String,
        val _object: String,
        val createdAt: Long,
        val threadId: String,
        val role: String,
        val content: List<Content>,
        // include other fields as needed
    )

    data class Content(
        val type: String,
        val text: TextContent
    )
    data class TextContent(
        val value: String,
        // include other fields as needed
    )

    fun convertJsonToSuggestionList(jsonString: String): SuggestionList? {
        try {
            // Extract the JSON array from the wrapped string
            Log.i(">>convertJsonToSuggestionList", "jsonString: $jsonString")
            val jsonString1 = jsonString.removePrefix("```json\n").removeSuffix("\n```")
            val jsonObject = JsonParser.parseString(jsonString1).asJsonObject
            Log.i(">>convertJsonToSuggestionList", "jsonObject: $jsonObject")
            val suggestionsJson = jsonObject.getAsJsonArray("suggestions")
            Log.i(">>convertJsonToSuggestionList", "suggestionsJson: $suggestionsJson.toString()")

            // Deserialize the JSON array to List<Suggestion>
            val suggestions = Gson().fromJson(suggestionsJson, Array<Suggestion>::class.java).toList()

            // Create a SuggestionList and update additional fields
            val suggestionList = SuggestionList(suggestions.map { suggestion ->
                suggestion.apply {
                    imgUrl = "" // Set default or compute value
                    streetAddress = parseAddress(address)
                    isChecked = false // Default value
                    btnEnabled = true // Default value
                }
            })

            return suggestionList

        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            return null
        }
    }
}
