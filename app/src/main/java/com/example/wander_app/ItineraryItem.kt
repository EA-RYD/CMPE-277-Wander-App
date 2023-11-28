package com.example.wander_app

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class ItineraryItem (
    val suggestionId: Integer,
    val locationId: String,
    val responseString: String,
    val locationName: String,
    val description: String,
    val imgUrl: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) {
    override fun toString(): String {
        return "ItineraryItem(suggestionId=$suggestionId, locationId=$locationId, responseString=$responseString, locationName=$locationName, description=$description, imgUrl=$imgUrl,id=$id)"
    }
}