package com.grand.duke.elliot.restaurantpost.repository

import com.grand.duke.elliot.restaurantpost.persistence.dao.FolderDao
import com.grand.duke.elliot.restaurantpost.persistence.dao.PlaceDao
import com.grand.duke.elliot.restaurantpost.persistence.dao.PostDao
import com.grand.duke.elliot.restaurantpost.persistence.dao.TagDao
import com.grand.duke.elliot.restaurantpost.repository.data.FolderWithPostList
import com.grand.duke.elliot.restaurantpost.repository.data.TagWithPostList
import io.reactivex.Flowable
import javax.inject.Inject

class LocalRepository @Inject constructor(
    private val folderDao: FolderDao,
    private val placeDao: PlaceDao,
    private val postDao: PostDao,
    private val tagDao: TagDao
) {
    @Suppress("SpellCheckingInspection")
    private val folderWithPostListFlowable: Flowable<List<FolderWithPostList>> by lazy {
        folderDao.getAll()
    }

    @Suppress("SpellCheckingInspection")
    fun folderWithPostListFlowable() = folderWithPostListFlowable

    @Suppress("SpellCheckingInspection")
    private val tagWithPostListFlowable: Flowable<List<TagWithPostList>> by lazy {
        tagDao.getAll()
    }

    @Suppress("SpellCheckingInspection")
    fun tagWithPostListFlowable() = tagWithPostListFlowable
}