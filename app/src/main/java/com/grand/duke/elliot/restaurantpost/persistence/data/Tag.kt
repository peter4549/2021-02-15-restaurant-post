package com.grand.duke.elliot.restaurantpost.persistence.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "tag")
@Parcelize
data class Tag (@PrimaryKey var name: String): Parcelable