package com.grand.duke.elliot.restaurantpost.persistence.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "place")
data class Place (
    @PrimaryKey
    var name: String,
    val latitude: Double,
    val longitude: Double
)