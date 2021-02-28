package com.grand.duke.elliot.restaurantpost.ui.home

import android.app.Application
import androidx.lifecycle.*
import com.grand.duke.elliot.restaurantpost.application.noFolderSelected
import com.grand.duke.elliot.restaurantpost.application.noPlaceSelected
import com.grand.duke.elliot.restaurantpost.application.shared_preferences.SharedPreferencesHelper
import com.grand.duke.elliot.restaurantpost.persistence.data.Folder
import com.grand.duke.elliot.restaurantpost.persistence.data.Place
import com.grand.duke.elliot.restaurantpost.persistence.data.Post
import com.grand.duke.elliot.restaurantpost.persistence.data.TagWithPostList
import com.grand.duke.elliot.restaurantpost.repository.LocalRepository
import com.grand.duke.elliot.restaurantpost.repository.data.DisplayTag
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(application: Application, localRepository: LocalRepository): ViewModel() {
    private val compositeDisposable by lazy {
        CompositeDisposable()
    }

    private val folderDao = localRepository.folderDao
    private val placeDao = localRepository.placeDao
    private val postDao = localRepository.postDao
    private val postTagCrossRefDao = localRepository.postTagCrossRefDao
    private val sharedPreferencesHelper = SharedPreferencesHelper(application)

    private val checkedTagIdSet = sharedPreferencesHelper.getCheckedTagIdSet().map { it.toLong() }.toSet()
    private val _checkedTagWithPostLists = MutableLiveData(mutableListOf<TagWithPostList>())
    val checkedTagWithPostLists: LiveData<MutableList<TagWithPostList>>
        get() = _checkedTagWithPostLists

    private val _selectedFolder = MutableLiveData<Folder>()
    val selectedFolder: LiveData<Folder>
        get() = _selectedFolder

    private var _selectedPlace = MutableLiveData<Place>()
    val selectedPlace: LiveData<Place>
        get() = _selectedPlace

    private var _postList = MutableLiveData<List<Post>>()
    val postList: LiveData<List<Post>>
        get() = _postList

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                checkedTagWithPostLists.value?.addAll(postTagCrossRefDao.getTagWithPostListsByTagIdSet(checkedTagIdSet))

                val folder = folderDao.get(sharedPreferencesHelper.getSelectedFolderId())
                val place = placeDao.get(sharedPreferencesHelper.getSelectedPlaceId())

                withContext(Dispatchers.Main) {
                    _selectedFolder.value = folder
                    _selectedPlace.value = place
                }
            }
        }

        compositeDisposable.add(postDao.getAll().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    _postList.value = it
                }, {
                    Timber.e(it)
                }))
    }

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

    fun filterPostList(postList: List<Post>): List<Post> {
        val filteredPostList = postList.filter {
            it.folderId == selectedFolder.value?.id ?: noFolderSelected
        }.filter {
            it.placeId == selectedPlace.value?.id ?: noPlaceSelected
        }

        return if (checkedTagWithPostLists.value?.isNotEmpty() == true)
            filteredPostList.filter { post -> (checkedTagWithPostLists.value ?: listOf())
                    .flatMap { it.postList }
                    .contains(post)
            }
        else
            filteredPostList
    }
    
    fun setSelectedFolder(folder: Folder) {
        _selectedFolder.value = folder
        invalidateLiveData()
    }
    
    fun setSelectedPlace(place: Place) {
        _selectedPlace.value = place
        invalidateLiveData()
    }
    
    fun addTagWithPostList(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val tagWithPostList = postTagCrossRefDao.getTagWithPostListByTagId(id)
                checkedTagWithPostLists.value?.add(tagWithPostList)
            }

            withContext(Dispatchers.Main) {
                invalidateLiveData()
            }
        }
    }

    fun invalidateLiveData() {
        val value = _postList.value
        _postList.value = value?.toList()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}