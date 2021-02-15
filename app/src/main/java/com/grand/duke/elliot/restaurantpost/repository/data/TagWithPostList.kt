package com.grand.duke.elliot.restaurantpost.repository.data

import androidx.room.Embedded
import androidx.room.Relation
import com.grand.duke.elliot.restaurantpost.persistence.data.Post
import com.grand.duke.elliot.restaurantpost.persistence.data.Tag

data class TagWithPostList(
    @Embedded
    val tag: Tag,
    @Relation(parentColumn = "name", entityColumn = "tags")
    val postList: List<Post>,
)