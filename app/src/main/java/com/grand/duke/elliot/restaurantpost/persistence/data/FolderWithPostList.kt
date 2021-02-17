package com.grand.duke.elliot.restaurantpost.persistence.data

import androidx.room.Embedded
import androidx.room.Relation
import com.grand.duke.elliot.restaurantpost.persistence.data.Folder
import com.grand.duke.elliot.restaurantpost.persistence.data.Post

class FolderWithPostList (
    @Embedded
    val folder: Folder,
    @Relation(parentColumn = "folder_id", entityColumn = "folder_id")
    val postList: List<Post>
)