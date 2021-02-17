package com.grand.duke.elliot.restaurantpost.persistence.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "tag")
@Parcelize
data class Tag (
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "tag_id") val id: Long = 0L,
        var name: String
): Parcelable {
        fun deepCopy() = Tag(
                id = this.id,
                name = this.name
        )
}