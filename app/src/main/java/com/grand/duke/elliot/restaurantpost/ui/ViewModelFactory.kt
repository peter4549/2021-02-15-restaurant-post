package com.grand.duke.elliot.restaurantpost.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class ViewModelFactory @Inject constructor(private val viewModelMap: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        var viewModel: Provider<out ViewModel>? = viewModelMap[modelClass]

        viewModel ?: run {
            for ((key, value) in viewModelMap) {
                if (modelClass.isAssignableFrom(key)) {
                    viewModel = value
                    break
                }
            }
        }

        viewModel?.let {
            try {
                @Suppress("UNCHECKED_CAST")
                return viewModel?.get() as T
            } catch (e: Exception) {
                throw IllegalStateException(e)
            }
        } ?: run { throw IllegalArgumentException("Unknown ViewModel class.") }
    }
}