package com.grand.duke.elliot.restaurantpost.persistence.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.grand.duke.elliot.restaurantpost.persistence.data.Post
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface PostDao {

    @Query("SELECT * FROM post ORDER BY modifiedTime DESC")
    fun getAll(): Flowable<List<Post>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(post: Post): Single<Long>

    @Delete
    fun delete(post: Post): Completable

    @Update
    fun update(post: Post): Completable
}