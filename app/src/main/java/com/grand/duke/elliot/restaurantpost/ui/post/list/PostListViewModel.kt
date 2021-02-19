package com.grand.duke.elliot.restaurantpost.ui.post.list

import androidx.lifecycle.ViewModel
import com.grand.duke.elliot.restaurantpost.repository.LocalRepository
import javax.inject.Inject

class PostListViewModel @Inject constructor(private val localRepository: LocalRepository): ViewModel() {

    private val postDao = localRepository.postDao

    fun postList() = postDao.getAll()
}