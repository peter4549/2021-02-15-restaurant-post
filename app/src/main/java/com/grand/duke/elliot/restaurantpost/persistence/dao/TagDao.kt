package com.grand.duke.elliot.restaurantpost.persistence.dao

import androidx.room.*
import com.grand.duke.elliot.restaurantpost.persistence.data.Tag
import io.reactivex.Completable

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(tag: Tag): Completable

    @Delete
    fun delete(tag: Tag)

    @Update
    fun update(tag: Tag)
}