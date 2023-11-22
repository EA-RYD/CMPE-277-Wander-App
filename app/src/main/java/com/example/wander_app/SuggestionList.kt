package com.example.wander_app

data class SuggestionList(
    val suggestions: List<Suggestion>
)

data class Suggestion(
    val img:String,
    val name: String,
    val address: String,
    val longitude: Double,
    val latitude: Double,
    val description: String
)

