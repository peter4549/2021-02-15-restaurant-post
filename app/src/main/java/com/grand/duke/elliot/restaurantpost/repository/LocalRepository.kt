package com.grand.duke.elliot.restaurantpost.repository

import com.grand.duke.elliot.restaurantpost.persistence.dao.FolderDao
import com.grand.duke.elliot.restaurantpost.persistence.dao.PlaceDao
import com.grand.duke.elliot.restaurantpost.persistence.dao.PostDao
import com.grand.duke.elliot.restaurantpost.persistence.dao.TagDao
import com.grand.duke.elliot.restaurantpost.repository.data.FolderWithPostList
import dagger.android.AndroidInjection
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton

class LocalRepository @Inject constructor(
    private val folderDao: FolderDao,
    private val placeDao: PlaceDao,
    private val postDao: PostDao,
    private val tagDao: TagDao
) {
    @Suppress("SpellCheckingInspection")
    private val postListInFolderFlowable: Flowable<List<FolderWithPostList>> by lazy {
        folderDao.getAll()
    }

    @Suppress("SpellCheckingInspection")
    fun postListInFolderFlowable() = postListInFolderFlowable
}