package com.grand.duke.elliot.restaurantpost.repository.data

import com.grand.duke.elliot.restaurantpost.persistence.data.Place

data class DisplayPlace (
        val place: Place,
        val postListCount: Int
) {
    fun deepCopy() = DisplayPlace(
            place = this.place.deepCopy(),
            postListCount = this.postListCount
    )
}