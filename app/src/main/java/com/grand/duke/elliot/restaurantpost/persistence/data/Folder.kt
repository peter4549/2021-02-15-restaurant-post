package com.grand.duke.elliot.restaurantpost.persistence.data

import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "folder")
@Parcelize
data class Folder (
    @PrimaryKey
    var name: String,
    @ColorInt
    var color: Int
): Parcelable {
    fun deepCopy() = Folder(
        name = this.name,
        color = this.color
    )
}