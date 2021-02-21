package com.grand.duke.elliot.restaurantpost.repository.data

import androidx.room.ColumnInfo

data class DisplayPlace (
        @ColumnInfo(name = "place_id") val id: Long,
        var name: String,
        val postListCount: Int
) {
    fun deepCopy() = DisplayPlace(
            id = this.id,
            name = this.name,
            postListCount = this.postListCount
    )
}