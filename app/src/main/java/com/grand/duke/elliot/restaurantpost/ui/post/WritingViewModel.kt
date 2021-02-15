package com.grand.duke.elliot.restaurantpost.ui.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grand.duke.elliot.restaurantpost.persistence.data.Place
import com.grand.duke.elliot.restaurantpost.persistence.data.Post
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

class WritingViewModel @AssistedInject constructor(@Assisted private val post: Post?): ViewModel() {
    @AssistedInject.Factory
    interface Factory {
        fun create(post: Post?): WritingViewModel
    }

    val modifiedTime: Long = System.currentTimeMillis()

    private val _place = MutableLiveData<Place?>(null)
    val place: LiveData<Place?>
        get() = _place

    fun setPlace(place: Place) {
        _place.value = place
    }
}