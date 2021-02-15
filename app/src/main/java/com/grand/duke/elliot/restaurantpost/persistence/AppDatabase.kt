package com.grand.duke.elliot.restaurantpost.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.grand.duke.elliot.restaurantpost.persistence.dao.FolderDao
import com.grand.duke.elliot.restaurantpost.persistence.dao.TagDao
import com.grand.duke.elliot.restaurantpost.persistence.dao.PlaceDao
import com.grand.duke.elliot.restaurantpost.persistence.dao.PostDao
import com.grand.duke.elliot.restaurantpost.persistence.data.Folder
import com.grand.duke.elliot.restaurantpost.persistence.data.Place
import com.grand.duke.elliot.restaurantpost.persistence.data.Post
import com.grand.duke.elliot.restaurantpost.persistence.data.Tag
import com.grand.duke.elliot.restaurantpost.repository.data.TagPostListCrossRef

@Database (entities = [Folder::class, Place::class, Post::class, Tag::class],
    version = 1, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun folderDao(): FolderDao
    abstract fun placeDao(): PlaceDao
    abstract fun postDao(): PostDao
    abstract fun tagDao(): TagDao

    companion object {
        const val name = "com.grand.duke.elliot.restaurantpost.database" +
                ".AppDatabase.name:debug.1.0.0"
    }
}