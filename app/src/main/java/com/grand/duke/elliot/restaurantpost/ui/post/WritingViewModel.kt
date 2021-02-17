package com.grand.duke.elliot.restaurantpost.ui.post

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grand.duke.elliot.restaurantpost.persistence.dao.FolderDao
import com.grand.duke.elliot.restaurantpost.persistence.dao.TagDao
import com.grand.duke.elliot.restaurantpost.persistence.data.Folder
import com.grand.duke.elliot.restaurantpost.persistence.data.Place
import com.grand.duke.elliot.restaurantpost.persistence.data.Post
import com.grand.duke.elliot.restaurantpost.persistence.data.Tag
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WritingViewModel @AssistedInject constructor(
        @Assisted private val post: Post?,
        @Assisted private val folderDao: FolderDao,
        @Assisted private val tagDao: TagDao
): ViewModel() {

    @AssistedInject.Factory
    interface Factory {
        fun create(post: Post?, folderDao: FolderDao, tagDao: TagDao): WritingViewModel
    }

    val modifiedTime: Long = System.currentTimeMillis()
    var folder: Folder? = null

    private val _place = MutableLiveData<Place?>(null)
    val place: LiveData<Place?>
        get() = _place

    private val _photoUris = MutableLiveData<MutableList<Uri>>(mutableListOf())
    val photoUris: LiveData<MutableList<Uri>>
        get() = _photoUris

    private val tags = mutableListOf<Tag>()

    fun tags() = tags

    fun addTag(tag: Tag) {
        tags.add(tag)
    }

    fun removeTag(tag: Tag) {
        tags.remove(tag)
    }

    fun updateTag(updatedTag: Tag) {
        for ((index, tag) in tags.withIndex())
            if (tag.id == updatedTag.id) {
                tags[index] = updatedTag
                break
            }
    }

    fun addPhotoUri(uri: Uri) {
        val value = photoUris.value
        value?.add(uri)
        _photoUris.value = value
    }

    fun setPlace(place: Place) {
        _place.value = place
    }

    fun deleteFolder(folder: Folder) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                folderDao.delete(folder)
            }
        }
    }

    fun deleteTag(tag: Tag) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                tagDao.delete(tag)
            }
        }
    }
}