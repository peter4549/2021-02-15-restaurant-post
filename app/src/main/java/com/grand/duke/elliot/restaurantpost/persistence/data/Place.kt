package com.grand.duke.elliot.restaurantpost.persistence.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "place")
data class Place (
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0L,
        var name: String,
        val latitude: Double,
        val longitude: Double
) {
    fun deepCopy() = Place(
            id = this.id,
            name = this.name,
            latitude = this.latitude,
            longitude = this.longitude
    )
}