package com.grand.duke.elliot.restaurantpost.repository

import com.grand.duke.elliot.restaurantpost.persistence.dao.*
import com.grand.duke.elliot.restaurantpost.persistence.data.FolderWithPostList
import com.grand.duke.elliot.restaurantpost.persistence.data.TagWithPostList
import io.reactivex.Flowable
import javax.inject.Inject

class LocalRepository @Inject constructor(
        val folderDao: FolderDao,
        val placeDao: PlaceDao,
        val postDao: PostDao,
        val tagDao: TagDao,
        val postTagCrossRefDao: PostTagCrossRefDao
) {
    @Suppress("SpellCheckingInspection")
    private val folderWithPostListFlowable: Flowable<List<FolderWithPostList>> by lazy {
        folderDao.getAll()
    }

    @Suppress("SpellCheckingInspection")
    fun folderWithPostListFlowable() = folderWithPostListFlowable

    @Suppress("SpellCheckingInspection")
    private val tagWithPostListFlowable: Flowable<List<TagWithPostList>> by lazy {
        postTagCrossRefDao.getTagWithPostLists()
    }

    @Suppress("SpellCheckingInspection")
    fun tagWithPostListFlowable() = tagWithPostListFlowable
}