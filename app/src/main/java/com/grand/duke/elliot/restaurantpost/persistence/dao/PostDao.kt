package com.grand.duke.elliot.restaurantpost.persistence.dao

import androidx.room.*
import com.grand.duke.elliot.restaurantpost.persistence.data.Post
import io.reactivex.Completable

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(post: Post): Completable

    @Delete
    fun delete(post: Post)

    @Update
    fun update(post: Post)
}