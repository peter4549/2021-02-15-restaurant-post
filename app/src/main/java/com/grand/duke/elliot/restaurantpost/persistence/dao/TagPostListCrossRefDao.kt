package com.grand.duke.elliot.restaurantpost.persistence.dao

import androidx.room.*
import com.grand.duke.elliot.restaurantpost.persistence.data.TagPostListCrossRef
import com.grand.duke.elliot.restaurantpost.persistence.data.TagWithPostList
import io.reactivex.Flowable

@Dao
interface TagPostListCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(tagPostListCrossRef: TagPostListCrossRef)

    @Transaction
    @Query("SELECT * FROM tag ORDER BY name ASC")
    fun getTagWithPostLists(): Flowable<List<TagWithPostList>>
}