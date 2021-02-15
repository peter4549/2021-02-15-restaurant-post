package com.grand.duke.elliot.restaurantpost.persistence.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "post")
data class Post (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    var folder: String,
    var modifiedTime: Long,
    var place: Place?,
    var photoUris: Array<String>,
    var tags: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Post

        if (id != other.id) return false
        if (folder != other.folder) return false
        if (place != other.place) return false
        if (!photoUris.contentEquals(other.photoUris)) return false
        if (!tags.contentEquals(other.tags)) return false
        if (modifiedTime != other.modifiedTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + folder.hashCode()
        result = 31 * result + (place?.hashCode() ?: 0)
        result = 31 * result + photoUris.contentHashCode()
        result = 31 * result + tags.contentHashCode()
        result = 31 * result + modifiedTime.hashCode()
        return result
    }

}