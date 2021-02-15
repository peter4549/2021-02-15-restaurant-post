package com.grand.duke.elliot.restaurantpost.repository.data

import com.grand.duke.elliot.restaurantpost.persistence.data.Folder

data class DisplayFolder(
    val folder: Folder,
    var postListCount: Int
) {
    fun deepCopy() = DisplayFolder(
        folder = this.folder.deepCopy(),
        postListCount = this.postListCount
    )
}