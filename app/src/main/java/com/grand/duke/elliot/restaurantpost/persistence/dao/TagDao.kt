package com.grand.duke.elliot.restaurantpost.persistence.dao

import androidx.room.*
import com.grand.duke.elliot.restaurantpost.persistence.data.Tag
import com.grand.duke.elliot.restaurantpost.repository.data.FolderWithPostList
import com.grand.duke.elliot.restaurantpost.repository.data.TagWithPostList
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface TagDao {
    @Transaction
    @Query("SELECT * FROM tag")
    fun getAll(): Flowable<List<TagWithPostList>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(tag: Tag): Completable

    @Delete
    fun delete(tag: Tag)

    @Update
    fun update(tag: Tag): Completable
}