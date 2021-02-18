package com.grand.duke.elliot.restaurantpost.ui.post

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grand.duke.elliot.restaurantpost.persistence.dao.FolderDao
import com.grand.duke.elliot.restaurantpost.persistence.dao.PostDao
import com.grand.duke.elliot.restaurantpost.persistence.dao.PostTagCrossRefDao
import com.grand.duke.elliot.restaurantpost.persistence.dao.TagDao
import com.grand.duke.elliot.restaurantpost.persistence.data.*
import com.grand.duke.elliot.restaurantpost.ui.util.isNotNull
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WritingViewModel @AssistedInject constructor(
        @Assisted private val post: Post?,
        @Assisted private val folderDao: FolderDao,
        @Assisted private val tagDao: TagDao,
        @Assisted private val postDao: PostDao,
        @Assisted private val postTagCrossRefDao: PostTagCrossRefDao
): ViewModel() {

    @AssistedInject.Factory
    interface Factory {
        fun create(post: Post?,
                   folderDao: FolderDao,
                   tagDao: TagDao,
                   postDao: PostDao,
                   postTagCrossRefDao: PostTagCrossRefDao
        ): WritingViewModel
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

    fun place() = _place.value

    private val _photoUriList = MutableLiveData<MutableList<Uri>>(mutableListOf())
    val photoUriList: LiveData<MutableList<Uri>>
        get() = _photoUriList

    private val existingPhotoUriArray: Array<Uri>

    fun existingPhotoUriArray() = existingPhotoUriArray
    fun photoUriList() = _photoUriList.value

    init {
        val photoUriList = mutableListOf<Uri>()

        post?.photoUris?.forEach { uri ->
            photoUriList.add(uri)
        }

        existingPhotoUriArray = photoUriList.map { it }.toTypedArray()
        _photoUriList.value = photoUriList
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
        val value = photoUriList.value
        value?.add(uri)
        _photoUriList.value = value
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

    private suspend fun insertTagList(post: Post) {
        postTagCrossRefDao.insertAll(tagList().map {
            PostTagCrossRef(
                    tagId = it.id,
                    postId = post.id
            )
        })
    }

    fun insertPost(post: Post) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                postDao.insert(post)
                insertTagList(post)
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