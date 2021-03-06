package com.grand.duke.elliot.restaurantpost.repository.data

import com.grand.duke.elliot.restaurantpost.persistence.data.Tag

data class DisplayTag(
    val tag: Tag,
    val postListCount: Int
) {
    fun deepCopy() = DisplayTag(
        tag = this.tag,
        postListCount = this.postListCount
    )
}