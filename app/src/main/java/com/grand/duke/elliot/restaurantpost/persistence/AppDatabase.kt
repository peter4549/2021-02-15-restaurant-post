package com.grand.duke.elliot.restaurantpost.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.grand.duke.elliot.restaurantpost.persistence.dao.*
import com.grand.duke.elliot.restaurantpost.persistence.data.*

@Database (entities = [Folder::class, Place::class, Post::class, Tag::class, TagPostListCrossRef::class],
    version = 1, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun folderDao(): FolderDao
    abstract fun placeDao(): PlaceDao
    abstract fun postDao(): PostDao
    abstract fun tagDao(): TagDao
    abstract fun tagPostListCrossRefDao(): TagPostListCrossRefDao

    companion object {
        const val name = "com.grand.duke.elliot.restaurantpost.database" +
                ".AppDatabase.name:debug.1.0.1"
    }
}