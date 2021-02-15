package com.grand.duke.elliot.restaurantpost.repository.data

import androidx.room.Embedded
import androidx.room.Relation
import com.grand.duke.elliot.restaurantpost.persistence.data.Folder
import com.grand.duke.elliot.restaurantpost.persistence.data.Post

class FolderWithPostList (
    @Embedded
    val folder: Folder,
    @Relation(parentColumn = "name", entityColumn = "folder")
    val postList: List<Post>
)