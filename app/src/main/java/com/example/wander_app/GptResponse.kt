package com.example.wander_app

data class GptResponse(
    val choices: List<Choice>
)

data class Choice(
    val index: Int,
    val message: ResponseMessage
)

data class ResponseMessage(
    val role: String,
    val content: String
)