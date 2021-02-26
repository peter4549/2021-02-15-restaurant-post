package com.grand.duke.elliot.restaurantpost.persistence

import android.net.Uri
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.grand.duke.elliot.restaurantpost.persistence.data.Place

class Converters {
    @Suppress("SpellCheckingInspection")
    private val gson = Gson()

    @TypeConverter
    fun stringArrayToJson(value: Array<String>): String = gson.toJson(value)

    @TypeConverter
    fun jsonToStringArray(value: String): Array<String> = gson.fromJson(value, Array<String>::class.java)

    @TypeConverter
    fun longArrayToJson(value: Array<Long>): String = gson.toJson(value)

    @TypeConverter
    fun jsonToLongArray(value: String): Array<Long> = gson.fromJson(value, Array<Long>::class.java)

    @TypeConverter
    fun placeToJson(value: Place): String = gson.toJson(value)

    @TypeConverter
    fun jsonToPlace(value: String): Place = gson.fromJson(value, Place::class.java)
}