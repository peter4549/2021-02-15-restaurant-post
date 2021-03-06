package com.grand.duke.elliot.restaurantpost.base

import android.content.DialogInterface
import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionInflater
import android.view.Window
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.grand.duke.elliot.restaurantpost.R
import dagger.android.AndroidInjection
import javax.inject.Inject


abstract class BaseActivity<VM : ViewModel, VDB : ViewDataBinding> : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    protected lateinit var viewModel: VM
    protected lateinit var viewDataBinding: VDB

    @get:LayoutRes
    protected abstract val layoutRes: Int

    protected abstract fun viewModel(): Class<VM>

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(viewModelStore, viewModelFactory).get(viewModel())
        viewDataBinding = DataBindingUtil.setContentView(this, layoutRes)
    }

    protected fun showToast(text: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(this, text, duration).show()
    }

    protected fun showMaterialAlertDialog(
            title: String?,
            message: String?,
            neutralButtonText: String?,
            neutralButtonClickListener: ((DialogInterface?, Int) -> Unit)?,
            negativeButtonText: String?,
            negativeButtonClickListener: ((DialogInterface?, Int) -> Unit)?,
            positiveButtonText: String?,
            positiveButtonClickListener: ((DialogInterface?, Int) -> Unit)?
    ) {
        MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(neutralButtonText, neutralButtonClickListener)
                .setNegativeButton(negativeButtonText, negativeButtonClickListener)
                .setPositiveButton(positiveButtonText, positiveButtonClickListener)
                .setCancelable(false)
                .show()
    }
}