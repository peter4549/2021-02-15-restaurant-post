package com.grand.duke.elliot.restaurantpost.persistence.data

import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.room.*
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "folder")
@Parcelize
data class Folder (
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "folder_id") val id: Long = 0L,
        var name: String,
        @ColorInt
        var color: Int
): Parcelable {
    fun deepCopy() = Folder(
            id = this.id,
            name = this.name,
            color = this.color
    )
}