package com.grand.duke.elliot.restaurantpost.persistence.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.grand.duke.elliot.restaurantpost.persistence.data.Place
import com.grand.duke.elliot.restaurantpost.persistence.data.PlaceWithPostList
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface PlaceDao {
    @Transaction
    @Query("SELECT * FROM place ORDER BY name ASC")
    fun getAll(): Flowable<List<PlaceWithPostList>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(place: Place): Completable

    @Delete
    fun delete(place: Place)

    @Update
    fun update(place: Place)

    @Query("SELECT * FROM place WHERE place_id = :id LIMIT 1")
    fun get(id: Long): Place?
}