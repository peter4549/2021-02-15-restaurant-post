package com.grand.duke.elliot.restaurantpost.repository

import androidx.lifecycle.LiveData
import com.grand.duke.elliot.restaurantpost.persistence.dao.*
import com.grand.duke.elliot.restaurantpost.persistence.data.Folder
import com.grand.duke.elliot.restaurantpost.persistence.data.FolderWithPostList
import com.grand.duke.elliot.restaurantpost.persistence.data.TagWithPostList
import io.reactivex.Flowable
import javax.inject.Inject

class LocalRepository @Inject constructor(
    private val folderDao: FolderDao,
    private val placeDao: PlaceDao,
    private val postDao: PostDao,
    private val tagDao: TagDao,
    private val tagPostListCrossRefDao: TagPostListCrossRefDao
) {
    @Suppress("SpellCheckingInspection")
    private val folderWithPostListFlowable: Flowable<List<FolderWithPostList>> by lazy {
        folderDao.getAll()
    }

    @Suppress("SpellCheckingInspection")
    fun folderWithPostListFlowable() = folderWithPostListFlowable

    @Suppress("SpellCheckingInspection")
    private val tagWithPostListFlowable: Flowable<List<TagWithPostList>> by lazy {
        tagPostListCrossRefDao.getTagWithPostLists()
    }

    @Suppress("SpellCheckingInspection")
    fun tagWithPostListFlowable() = tagWithPostListFlowable
}