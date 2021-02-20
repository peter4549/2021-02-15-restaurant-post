package com.grand.duke.elliot.restaurantpost.ui.post.writing

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grand.duke.elliot.restaurantpost.persistence.data.*
import com.grand.duke.elliot.restaurantpost.repository.LocalRepository
import com.grand.duke.elliot.restaurantpost.ui.util.difference
import com.grand.duke.elliot.restaurantpost.ui.util.isNotNull
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WritingViewModel @AssistedInject constructor(
        @Assisted private val post: Post?,
        @Assisted private val localRepository: LocalRepository
): ViewModel() {

    private val folderDao = localRepository.folderDao
    private val postDao = localRepository.postDao
    private val postTagCrossRefDao = localRepository.postTagCrossRefDao
    private val tagDao = localRepository.tagDao

    @AssistedInject.Factory
    interface Factory {
        fun create(post: Post?, localRepository: LocalRepository): WritingViewModel
    }

    private val compositeDisposable by lazy {
        CompositeDisposable()
    }

    private val mode =
            if (post.isNotNull())
                Mode.Edit
            else
                Mode.Creation

    fun mode() = mode

    fun post() = post

    val modifiedTime: Long = post?.modifiedTime ?: System.currentTimeMillis()

    private val _folder = MutableLiveData<Folder?>(null)
    val folder: LiveData<Folder?>
        get() = _folder

    fun folder() = _folder.value

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                post?.let {
                    _folder.value = folderDao.folder(it.folderId)
                }
            }
        }
    }

    fun setFolder(folder: Folder?) {
        _folder.value = folder
    }

    private val _place = MutableLiveData<Place?>(post?.place?.deepCopy())
    val place: LiveData<Place?>
        get() = _place

    private val existingPlace = post?.place?.deepCopy()

    fun existingPlace() = existingPlace
    fun place() = _place.value

    private val _photoUriStringList = MutableLiveData<MutableList<String>>(mutableListOf())
    val photoUriStringList: LiveData<MutableList<String>>
        get() = _photoUriStringList

    private val existingPhotoUriStringArray: Array<String>

    fun existingPhotoUriArray() = existingPhotoUriStringArray
    fun photoUriList() = _photoUriStringList.value ?: listOf()

    init {
        val photoUriStringList = mutableListOf<String>()

        post?.photoUriStringArray?.forEach {
            photoUriStringList.add(it)
        }

        existingPhotoUriStringArray = photoUriStringList.map { it }.toTypedArray()
        _photoUriStringList.value = photoUriStringList
    }


    private lateinit var existingTagArray: Array<Tag>
    private val _displayTagList = MutableLiveData(DisplayTagList(
            null,
            DisplayTagList.ChangeType.Initialized,
            mutableListOf())
    )
    val displayTagList: LiveData<DisplayTagList>
        get() = _displayTagList

    fun existingTagArray() = existingTagArray
    fun tagList() = displayTagList.value?.tagList ?: mutableListOf()

    init {
        if (post != null) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val value = mutableListOf<Tag>()
                    existingTagArray = postTagCrossRefDao
                            .getPostWithTagList(post.id)?.tagList?.toTypedArray() ?: arrayOf()
                    existingTagArray.forEach { value.add(it.deepCopy()) }
                    _displayTagList.value = DisplayTagList(
                            changedTag = null,
                            changeType = DisplayTagList.ChangeType.Initialized,
                            tagList = value
                    )
                }
            }
        }
    }

    fun addTag(tag: Tag) {
        val value = _displayTagList.value ?: return

        if (value.tagList.contains(tag))
            return

        value.apply {
            this.changedTag = tag
            this.changeType = DisplayTagList.ChangeType.Add
            this.tagList.add(tag)
        }
        _displayTagList.value = value
    }

    fun removeTag(tag: Tag) {
        val value = _displayTagList.value
        value?.apply {
            this.changedTag = tag
            this.changeType = DisplayTagList.ChangeType.Remove
            this.tagList.remove(tag)
        }
        _displayTagList.value = value
    }

    fun updateTag(tag: Tag) {
        val tagList: MutableList<Tag> = _displayTagList.value?.tagList ?: return

        for ((index, value) in tagList.withIndex())
            if (value.id == tag.id) {
                tagList[index] = tag
                break
            }

        val value = _displayTagList.value
        value?.apply {
            this.changedTag = tag
            this.changeType = DisplayTagList.ChangeType.Update
            this.tagList = tagList
        }
        _displayTagList.value = value
    }

    fun addPhotoUri(uri: Uri) {
        val value = photoUriStringList.value
        value?.add(uri.toString())
        _photoUriStringList.value = value
    }

    fun setPlace(place: Place) {
        _place.value = place
    }

    fun deleteFolder(folder: Folder) {
        viewModelScope.launch {
            if (folder.id == _folder.value?.id)
                _folder.value = null

            withContext(Dispatchers.IO) {
                folderDao.delete(folder)
            }
        }
    }

    fun deleteTag(tag: Tag) {
        viewModelScope.launch {
            val tagList = _displayTagList.value?.tagList ?: listOf()
            val tagIdList = tagList.map { it.id }

            if (tagIdList.contains(tag.id))
                removeTag(tag)

            withContext(Dispatchers.IO) {
                tagDao.delete(tag)
            }
        }
    }

    private suspend fun insertTagList(postId: Long) {
        postTagCrossRefDao.insertAll(tagList().map {
            PostTagCrossRef(
                    tagId = it.id,
                    postId = postId
            )
        })
    }

    fun insertPost(post: Post, onComplete: (Throwable?) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                postDao.insert(post)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ postId ->
                            onComplete(null)
                            viewModelScope.launch {
                                withContext(Dispatchers.IO) {
                                    insertTagList(postId)
                                }
                            }
                        }, {
                            onComplete(it)
                        })

            }
        }
    }

    fun updatePost(post: Post, onComplete: (Throwable?) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                postDao.update(post)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            onComplete(null)
                        }, {
                            onComplete(it)
                        })

                val difference = existingTagArray().toList().difference(tagList())

                postTagCrossRefDao.deleteAll(difference.first.map {
                    PostTagCrossRef(
                            postId = post.id,
                            tagId = it.id
                    )
                })

                postTagCrossRefDao.insertAll(difference.second.map {
                    PostTagCrossRef(
                        postId = post.id,
                        tagId = it.id
                    )
                })
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    object Mode {
        const val Creation = 0
        const val Edit = 1
    }
}

data class DisplayTagList(
        var changedTag: Tag?,
        var changeType: Int,
        var tagList: MutableList<Tag>,
) {
    object ChangeType {
        const val Initialized = 0
        const val Add = 1
        const val Remove = 2
        const val Update = 3
    }
}