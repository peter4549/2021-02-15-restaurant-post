package com.grand.duke.elliot.restaurantpost.persistence.dao

import androidx.room.*
import com.grand.duke.elliot.restaurantpost.persistence.data.Post
import com.grand.duke.elliot.restaurantpost.persistence.data.PostTagCrossRef
import com.grand.duke.elliot.restaurantpost.persistence.data.PostWithTagList
import com.grand.duke.elliot.restaurantpost.persistence.data.TagWithPostList
import io.reactivex.Flowable

@Dao
interface PostTagCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(postTagCrossRef: PostTagCrossRef)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(postTagCrossRefList: List<PostTagCrossRef>)

    @Delete
    suspend fun deleteAll(postTagCrossRefList: List<PostTagCrossRef>)

    @Transaction
    @Query("SELECT * FROM tag ORDER BY name ASC")
    fun getTagWithPostLists(): Flowable<List<TagWithPostList>>

    @Transaction
    @Query("SELECT * FROM tag WHERE tag_id = :tagId")
    fun getTagWithPostListByTagId(tagId: Long): TagWithPostList

    @Transaction
    @Query("SELECT * FROM tag WHERE tag_id IN (:tagIdSet)")
    fun getTagWithPostListsByTagIdSet(tagIdSet: Set<Long>): List<TagWithPostList>

    @Transaction
    @Query("SELECT * FROM post ORDER BY modifiedTime DESC")
    fun getPostWithTagLists(): Flowable<List<PostWithTagList>>

    @Query("SELECT * FROM post WHERE post_id = :id")
    suspend fun getPostWithTagList(id: Long): PostWithTagList?
}