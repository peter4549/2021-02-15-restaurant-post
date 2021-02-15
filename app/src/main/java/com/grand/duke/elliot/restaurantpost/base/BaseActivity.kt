package com.grand.duke.elliot.restaurantpost.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.android.AndroidInjection
import javax.inject.Inject

abstract class BaseActivity<viewModel: ViewModel, viewDataBinding: ViewDataBinding> : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    protected lateinit var viewModel: viewModel
    protected lateinit var viewDataBinding: viewDataBinding

    @get:LayoutRes
    protected abstract val layoutRes: Int

    protected abstract fun viewModel(): Class<viewModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(viewModelStore, viewModelFactory).get(viewModel())
        viewDataBinding = DataBindingUtil.setContentView(this, layoutRes)
    }
}