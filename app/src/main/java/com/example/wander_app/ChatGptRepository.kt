package com.tutorial.chatgptapp
import android.util.Log
import android.widget.Toast
import com.example.wander_app.Suggestion
import com.example.wander_app.SuggestionList
import com.example.wander_app.parseAddress
import com.example.wander_app.updateAddressInSuggestions
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException



class ChatGptRepository() {
    val apiKey: String
    var chatGptTreadId: String
    val chatGptAssistantId: String
    var initialMessage: String

    init {
        apiKey = ""
        chatGptTreadId = ""
        chatGptAssistantId = "asst_yB7SSMUnQze5Ten1oKyNgjbH"
        initialMessage = """
        {
            "assistant_id":"$chatGptAssistantId",
            "thread": {
                "messages": [
                    {"role": "user", "content":"You are working as a module in an application to provide travel suggestions for the users. Your response should always be in valid JSON format which include exactly 6 suggestions, and for each suggestion always include name, address(should not be none and street address comes first), and longitude and latitude and a short description(no more than 30 words). Stay with the defined json format strictly. Use address information(if not nan) in the uploaded file only if the travel location is San Diego."},
                    {"role": "user", "content":"Json response should always have every key and value specified here. You have full access to the uploaded files. Do not reply any thing else except the json. I will send you the travel location and preference in the following messages, always based on the last location or preference to provide suggestion json."}
                ]
            }
        }
    """.trimIndent()
    }

    fun addMessage(message: String) {
        val stringBuilder = StringBuilder(initialMessage)
        val newMessage = """{"role": "user", "content": "$message"}"""
        val insertIndex = stringBuilder.lastIndexOf("]")
        stringBuilder.insert(insertIndex, ",\n$newMessage")
        initialMessage = stringBuilder.toString()
    }
    fun callCreateThreadApi() {
        Log.i(">>callCreateThreadApi", "callCreateThreadApi: starting..")
        val client = OkHttpClient()
        val url = "https://api.openai.com/v1/threads/runs"

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = initialMessage.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .addHeader("OpenAI-Beta", "assistants=v1")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                var responseBodyString = ""
                if (!response.isSuccessful) {
                    responseBodyString = response.body?.string().toString()
//                   Log.i(">>callCreateThreadApi", "Fail Response: $responseBodyString")

                } else {
                    responseBodyString = response.body?.string().toString()
//                    Log.i(">>callCreateThreadApi", "Success Response: $responseBodyString")
                    chatGptTreadId = getThreadIdFromResponse(responseBodyString!!).toString()
                    Log.i(">>callCreateThreadApi", "UpdatedChatGptTreadId: $chatGptTreadId")
                }
            }
        })
    }


    fun getThreadIdFromResponse(response: String): String? {
        try {
            val jsonObject = JSONObject(response)
            return jsonObject.optString("thread_id", null) // Returns null if the key is not found
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
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
//                Log.i(">>callSendMessageApi", "Response: ${response.body?.string()}")
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
//                Log.i(">>callRunMessage", "Response: ${response.body?.string()}")
            }
        })
    }

    fun initialRetrieveApi() {
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
                        Log.i(">>callRetrieveApi", "Response: ${response.body?.string()}")
                    }
                }
            })
        }
    }
    fun callRetrieveApi(onFinish: (SuggestionList) -> Unit) {
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
                                            if (value.isNotEmpty() && role == "assistant"){
//                                                Log.i(">>callRetrieveApi", "Value: $value")

                                                // put suggestion to the raw suggestion list
                                                val suggestions = convertJsonToSuggestionList(value)
                                                onFinish(suggestions!!)
                                                Log.i(">>callRetrieveApi", "Suggestion: $suggestions")
                                            } else {
                                                Log.i(">>callRetrieveApi", "No valid response come back")
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
//                        Log.e(">>callRetrieveApi", "API call was not successful, Response JSON: ${response.body?.string()}")
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
        val data: List<Message>,
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
        val text: TextContent,
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
                    isChecked = false // Default value
                    btnEnabled = false // Default value
                }
            })
            updateAddressInSuggestions(suggestionList)
            Log.i(">>convertJsonToSuggestionList", "suggestionList: $suggestionList")

            return suggestionList

        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            return null
        }
    }
}
