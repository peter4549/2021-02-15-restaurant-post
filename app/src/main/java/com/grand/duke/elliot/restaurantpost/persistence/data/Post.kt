package com.grand.duke.elliot.restaurantpost.persistence.data

import android.net.Uri
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "post")
@Parcelize
data class Post (
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "post_id") val id: Long = 0L,
        var description: String,
        @ColumnInfo(name = "folder_id") var folderId: Long,
        var modifiedTime: Long,
        var photoUriStringArray: Array<String>,
        @ColumnInfo(name = "place_id") var placeId: Long
): Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Post

        if (id != other.id) return false
        if (description != other.description) return false
        if (folderId != other.folderId) return false
        if (modifiedTime != other.modifiedTime) return false
        if (!photoUriStringArray.contentEquals(other.photoUriStringArray)) return false
        if (placeId != other.placeId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + folderId.hashCode()
        result = 31 * result + modifiedTime.hashCode()
        result = 31 * result + photoUriStringArray.contentHashCode()
        result = 31 * result + placeId.hashCode()
        return result
    }
}