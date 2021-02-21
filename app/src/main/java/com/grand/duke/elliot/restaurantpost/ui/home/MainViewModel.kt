package com.grand.duke.elliot.restaurantpost.ui.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grand.duke.elliot.restaurantpost.application.shared_preferences.SharedPreferencesHelper
import com.grand.duke.elliot.restaurantpost.repository.LocalRepository
import javax.inject.Inject

class MainViewModel @Inject constructor(application: Application, localRepository: LocalRepository): ViewModel() {
    private val postDao = localRepository.postDao

    fun postList() = postDao.getAll()

    private val sharedPreferencesHelper = SharedPreferencesHelper(application)

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
}