package com.grand.duke.elliot.restaurantpost.persistence.data

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["tag_id", "post_id"])
data class TagPostListCrossRef(
        @ColumnInfo(name = "tag_id") val tagId: Long,
        @ColumnInfo(name = "post_id") val postId: Long
)