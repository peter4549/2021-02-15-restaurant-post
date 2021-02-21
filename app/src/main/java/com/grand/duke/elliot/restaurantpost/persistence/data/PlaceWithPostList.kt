package com.grand.duke.elliot.restaurantpost.persistence.data

import androidx.room.Embedded
import androidx.room.Relation

data class PlaceWithPostList (
    @Embedded
    val place: Place,
    @Relation(parentColumn = "place_id", entityColumn = "place_id")
    val postList: List<Post>
)