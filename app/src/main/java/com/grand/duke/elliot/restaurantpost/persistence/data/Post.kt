package com.grand.duke.elliot.restaurantpost.persistence.data

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "post")
data class Post (
    @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "post_id") val id: Long,
    @ColumnInfo(name = "folder_id") var folderId: Long,
    var modifiedTime: Long,
    var place: Place?,
    var photoUris: Array<Uri>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Post

        if (id != other.id) return false
        if (folderId != other.folderId) return false
        if (modifiedTime != other.modifiedTime) return false
        if (place != other.place) return false
        if (!photoUris.contentEquals(other.photoUris)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + folderId.hashCode()
        result = 31 * result + modifiedTime.hashCode()
        result = 31 * result + (place?.hashCode() ?: 0)
        result = 31 * result + photoUris.contentHashCode()
        return result
    }
}