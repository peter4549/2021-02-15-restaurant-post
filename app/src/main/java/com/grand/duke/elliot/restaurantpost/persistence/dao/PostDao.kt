package com.grand.duke.elliot.restaurantpost.persistence.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.grand.duke.elliot.restaurantpost.persistence.data.Post
import io.reactivex.Completable

@Dao
interface PostDao {

    @Query("SELECT * FROM post ORDER BY modifiedTime DESC")
    fun getAll(): LiveData<List<Post>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(post: Post): Completable

    @Delete
    fun delete(post: Post)

    @Update
    fun update(post: Post): Completable
}