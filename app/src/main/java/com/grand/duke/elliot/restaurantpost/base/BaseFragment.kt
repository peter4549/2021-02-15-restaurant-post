package com.grand.duke.elliot.restaurantpost.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.grand.duke.elliot.restaurantpost.ui.home.MainViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

abstract class BaseFragment<viewModel: ViewModel, viewDataBinding: ViewDataBinding>: Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    protected lateinit var viewModel: viewModel
    protected lateinit var viewDataBinding: viewDataBinding

    protected abstract val useSharedViewModel: Boolean

    @get:LayoutRes
    protected abstract val layoutRes: Int

    protected abstract fun viewModel(): Class<viewModel>

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = if (useSharedViewModel)
            requireActivity().run { ViewModelProviders.of(this)[viewModel()] }
        else
            ViewModelProvider(viewModelStore, viewModelFactory).get(viewModel())

        viewDataBinding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        return viewDataBinding.root
    }

    protected fun showToast(text: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(requireContext(), text, duration).show()
    }
}