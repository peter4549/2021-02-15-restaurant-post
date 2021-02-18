package com.grand.duke.elliot.restaurantpost.persistence.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PostWithTagList(
        @Embedded
    val post: Post,
        @Relation(
            parentColumn = "post_id",
            entity = Tag::class,
            entityColumn = "tag_id",
            associateBy = Junction(
                    value = PostTagCrossRef::class,
                    parentColumn = "post_id",
                    entityColumn = "tag_id"
            )
    )
    val tagList: List<Tag>,
)