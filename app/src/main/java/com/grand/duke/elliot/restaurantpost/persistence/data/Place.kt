package com.grand.duke.elliot.restaurantpost.persistence.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "place")
data class Place (
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "place_id") val id: Long = 0,
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

    fun contentEquals(place: Place): Boolean {
        if (this.name != place.name)
            return false

        if (this.latitude != place.latitude)
            return false

        if (this.longitude != place.longitude)
            return false

        return true
    }
}