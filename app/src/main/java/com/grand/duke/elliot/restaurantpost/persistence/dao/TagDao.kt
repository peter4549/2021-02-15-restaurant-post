package com.grand.duke.elliot.restaurantpost.persistence.dao

import androidx.room.*
import com.grand.duke.elliot.restaurantpost.persistence.data.Tag
import com.grand.duke.elliot.restaurantpost.persistence.data.TagWithPostList
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(tag: Tag): Completable

    @Delete
    suspend fun delete(tag: Tag)

    @Update
    fun update(tag: Tag): Completable
}