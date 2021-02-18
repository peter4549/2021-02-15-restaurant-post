package com.grand.duke.elliot.restaurantpost.persistence.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TagWithPostList(
    @Embedded
    val tag: Tag,
    @Relation(
            parentColumn = "tag_id",
            entity = Post::class,
            entityColumn = "post_id",
            associateBy = Junction(
                    value = PostTagCrossRef::class,
                    parentColumn = "tag_id",
                    entityColumn = "post_id"
            )
    )
    val postList: List<Post>,
)