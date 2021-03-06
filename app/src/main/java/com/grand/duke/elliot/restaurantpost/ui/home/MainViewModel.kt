package com.grand.duke.elliot.restaurantpost.ui.home

import android.app.Application
import androidx.lifecycle.*
import com.grand.duke.elliot.restaurantpost.application.noFolderSelected
import com.grand.duke.elliot.restaurantpost.application.noPlaceSelected
import com.grand.duke.elliot.restaurantpost.application.shared_preferences.SharedPreferencesHelper
import com.grand.duke.elliot.restaurantpost.persistence.data.*
import com.grand.duke.elliot.restaurantpost.repository.LocalRepository
import com.grand.duke.elliot.restaurantpost.ui.post.list.PostAdapter
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

    private val _clickedPostAndViewHolder = MutableLiveData<PostAndViewHolder>()
    val clickedPostAndViewHolder: LiveData<PostAndViewHolder>
        get() = _clickedPostAndViewHolder

    fun setClickedPostAndViewHolder(postAndViewHolder: PostAndViewHolder) {
        _clickedPostAndViewHolder.value = postAndViewHolder
    }

    private val folderDao = localRepository.folderDao
    private val placeDao = localRepository.placeDao
    private val postDao = localRepository.postDao
    private val postTagCrossRefDao = localRepository.postTagCrossRefDao
    private val sharedPreferencesHelper = SharedPreferencesHelper(application)

    private val _checkedTagWithPostLists = MutableLiveData(mutableListOf<TagWithPostList>())

    private val _checkedTag = MutableLiveData<Tag>()
    val checkedTag: LiveData<Tag>
        get() = _checkedTag

    private val _uncheckedTag = MutableLiveData<Tag>()
    val uncheckedTag: LiveData<Tag>
        get() = _uncheckedTag

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
                _checkedTagWithPostLists.value?.addAll(
                    postTagCrossRefDao
                        .getTagWithPostListsByTagIdSet(
                            sharedPreferencesHelper.getCheckedTagIdSet().map { it.toLong() }.toSet()
                        )
                )

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

        return if (_checkedTagWithPostLists.value?.isNotEmpty() == true)
            filteredPostList.filter { post -> (_checkedTagWithPostLists.value ?: listOf())
                    .flatMap { it.postList }
                    .contains(post)
            }
        else
            filteredPostList
    }
    
    fun setSelectedFolder(folder: Folder?) {
        if (_selectedFolder.value == folder)
            return

        _selectedFolder.value = folder
        invalidate()
    }
    
    fun setSelectedPlace(place: Place?) {
        if (_selectedPlace.value == place)
            return

        _selectedPlace.value = place
        invalidate()
    }
    
    fun addTagWithPostList(id: Long) {
        val value = _checkedTagWithPostLists.value ?: mutableListOf()

        if (value.map { it.tag.id }.contains(id))
            return

        viewModelScope.launch {
            val addedTag: Tag?

            withContext(Dispatchers.IO) {
                val tagWithPostList = postTagCrossRefDao.getTagWithPostListByTagId(id)
                addedTag = tagWithPostList.tag
                value.add(tagWithPostList)
            }

            withContext(Dispatchers.Main) {
                _checkedTagWithPostLists.value = value
                _checkedTag.value = addedTag
                invalidate()
            }
        }
    }

    fun removeTagWithPostList(id: Long) {
        val value = _checkedTagWithPostLists.value ?: mutableListOf()

        value.singleOrNull { it.tag.id == id }?.let {
            value.remove(it)
            _checkedTagWithPostLists.value = value
            _uncheckedTag.value = it.tag
            invalidate()
        }
    }

    private fun invalidate() {
        val value = _postList.value
        _postList.value = value?.toList()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}

data class PostAndViewHolder(val post: Post, val viewHolder: PostAdapter.ViewHolder)