package com.grand.duke.elliot.restaurantpost.persistence.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tag")
data class Tag (@PrimaryKey val name: String)