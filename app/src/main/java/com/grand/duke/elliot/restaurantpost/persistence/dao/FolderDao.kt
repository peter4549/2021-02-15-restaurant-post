package com.grand.duke.elliot.restaurantpost.persistence.dao

import androidx.room.*
import com.grand.duke.elliot.restaurantpost.persistence.data.Folder
import com.grand.duke.elliot.restaurantpost.repository.data.FolderWithPostList
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface FolderDao {
    @Transaction
    @Query("SELECT * FROM folder")
    fun getAll(): Flowable<List<FolderWithPostList>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(folder: Folder)

    @Delete
    suspend fun delete(folder: Folder)

    @Update
    fun update(folder: Folder): Completable
}