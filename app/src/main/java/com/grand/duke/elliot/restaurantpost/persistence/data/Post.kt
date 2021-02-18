package com.grand.duke.elliot.restaurantpost.persistence.data

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "post")
data class Post (
    @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "post_id") val id: Long = 0L,
    var description: String,
    @ColumnInfo(name = "folder_id") var folderId: Long,
    var modifiedTime: Long,
    var photoUris: Array<Uri>,
    var place: Place?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Post

        if (id != other.id) return false
        if (description != other.description) return false
        if (folderId != other.folderId) return false
        if (modifiedTime != other.modifiedTime) return false
        if (!photoUris.contentEquals(other.photoUris)) return false
        if (place != other.place) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + folderId.hashCode()
        result = 31 * result + modifiedTime.hashCode()
        result = 31 * result + photoUris.contentHashCode()
        result = 31 * result + (place?.hashCode() ?: 0)
        return result
    }
}