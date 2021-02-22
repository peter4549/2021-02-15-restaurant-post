package com.grand.duke.elliot.restaurantpost.base

import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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