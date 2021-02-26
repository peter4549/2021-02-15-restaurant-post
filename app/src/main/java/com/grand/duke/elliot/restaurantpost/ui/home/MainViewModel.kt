package com.grand.duke.elliot.restaurantpost.ui.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grand.duke.elliot.restaurantpost.application.shared_preferences.SharedPreferencesHelper
import com.grand.duke.elliot.restaurantpost.persistence.data.Post
import com.grand.duke.elliot.restaurantpost.persistence.data.TagWithPostList
import com.grand.duke.elliot.restaurantpost.repository.LocalRepository
import javax.inject.Inject

class MainViewModel @Inject constructor(application: Application, localRepository: LocalRepository): ViewModel() {
    private val postDao = localRepository.postDao
    private val postTagCrossRefDao = localRepository.postTagCrossRefDao

    private val checkedTagWithPostLists = mutableListOf<TagWithPostList>()

    private val sharedPreferencesHelper = SharedPreferencesHelper(application)
    private val filterOptions: FilterOptions by lazy {
        val selectedFolderId = sharedPreferencesHelper.getSelectedFolderId()
        val selectedPlaceId = sharedPreferencesHelper.getSelectedPlaceId()

        FilterOptions(
                selectedFolderId,
                selectedPlaceId
        )
    }

    private var _postList = postDao.getAll()
    val postList: LiveData<List<Post>>
        get() = _postList

    @Suppress("SpellCheckingInspection")
    private val folderWithPostListFlowable = localRepository.folderWithPostListFlowable()
    @Suppress("SpellCheckingInspection")
    fun folderWithPostListFlowable() = folderWithPostListFlowable

    @Suppress("SpellCheckingInspection")
    private val tagWithPostListFlowable = localRepository.tagWithPostListFlowable()
    @Suppress("SpellCheckingInspection")
    fun tagWithPostListFlowable() = tagWithPostListFlowable

    @Suppress("SpellCheckingInspection")
    private val placeWithPostListFlowable = localRepository.placeWithPostListFlowable()
    @Suppress("SpellCheckingInspection")
    fun placeWithPostListFlowable() = placeWithPostListFlowable

    private val _checkedTagIdHashSet = MutableLiveData<HashSet<Long>>()
    val checkedTagIdHashSet: LiveData<HashSet<Long>>
        get() = _checkedTagIdHashSet

    init {
        val checkedTagIdList = sharedPreferencesHelper.getCheckedTagIdSet().map { it.toLong() }
        _checkedTagIdHashSet.value = checkedTagIdList.toHashSet()
    }

    fun addCheckedTagId(id: Long) {
        val value = checkedTagIdHashSet.value
        value?.add(id)
        _checkedTagIdHashSet.value = value
    }

    fun filterPostList(postList: List<Post>): List<Post> {
        return postList.filter { it.folderId == filterOptions.folderId }
                .filter { it.placeId == filterOptions.placeId }
                .filter { post -> checkedTagWithPostLists.flatMap { it.postList }.contains(post) }
    }

    /*
    fun submitFilteredPostList(filterOptions: FilterOptions) {
        val folderId = filterOptions.folderId
        val placeId = filterOptions.placeId
        val tagIdSet = filterOptions.tagIdSet

        viewModelScope.launch {
            val postList = ArrayList<Post>()

            withContext(Dispatchers.IO) {
                if (tagIdSet.isEmpty()) {
                    postList.addAll(postDao.getAllByFolderIdAndPlaceId(folderId, placeId))
                } else {
                    postList.addAll(postTagCrossRefDao.getTagWithPostListsByTagIdList(tagIdSet).flatMap { it.postList })
                    postList.filter { it.folderId == folderId && it.placeId == placeId }
                }
            }

            withContext(Dispatchers.Main) {
                _postList.value = postList
            }
        }
    }

     */
}

data class FilterOptions(
     var folderId: Long,
     var placeId: Long
)