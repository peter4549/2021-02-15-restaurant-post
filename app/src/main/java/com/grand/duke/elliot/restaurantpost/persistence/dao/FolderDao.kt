package com.grand.duke.elliot.restaurantpost.persistence.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.grand.duke.elliot.restaurantpost.persistence.data.Folder
import com.grand.duke.elliot.restaurantpost.persistence.data.FolderWithPostList
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface FolderDao {
    @Transaction
    @Query("SELECT * FROM folder ORDER BY name ASC")
    fun getAll(): Flowable<List<FolderWithPostList>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(folder: Folder)

    @Delete
    suspend fun delete(folder: Folder)

    @Update
    fun update(folder: Folder): Completable

    @Query("SELECT * FROM folder WHERE folder_id = :id ")
    suspend fun folder(id: Long): Folder
}