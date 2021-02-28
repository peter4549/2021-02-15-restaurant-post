package com.grand.duke.elliot.restaurantpost.persistence.dao

import androidx.room.*
import com.grand.duke.elliot.restaurantpost.persistence.data.Place
import com.grand.duke.elliot.restaurantpost.persistence.data.PlaceWithPostList
import com.grand.duke.elliot.restaurantpost.ui.post.location.PlaceContent
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
    suspend fun delete(place: Place)

    @Update
    fun update(place: Place): Completable

    @Query("SELECT * FROM place WHERE place_id = :id LIMIT 1")
    suspend fun get(id: Long): Place?

    @Query("SELECT name, latitude, longitude FROM place")
    suspend fun getAllPlaceContentList(): List<PlaceContent>
}