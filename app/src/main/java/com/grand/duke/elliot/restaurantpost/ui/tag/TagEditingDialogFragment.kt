package com.grand.duke.elliot.restaurantpost.ui.tag

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StyleRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.grand.duke.elliot.restaurantpost.R
import com.grand.duke.elliot.restaurantpost.application.MainApplication
import com.grand.duke.elliot.restaurantpost.databinding.FragmentTagEditingDialogBinding
import com.grand.duke.elliot.restaurantpost.persistence.dao.TagDao
import com.grand.duke.elliot.restaurantpost.persistence.data.Tag
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

class TagEditingDialogFragment: DialogFragment() {

    @Inject
    lateinit var tagDao: TagDao

    private lateinit var binding: FragmentTagEditingDialogBinding

    private val compositeDisposable by lazy {
        CompositeDisposable()
    }

    private var tag: Tag? = null
    private var textAppearance: Int = -1

    fun setTag(tag: Tag) {
        this.tag = tag
    }

    fun setTextAppearance(@StyleRes textAppearance: Int) {
        this.textAppearance = textAppearance
    }

    private var onTagUpdatedListener: OnTagUpdatedListener? = null

    interface OnTagUpdatedListener {
        fun onTagUpdated(tag: Tag)
        fun onError(throwable: Throwable)
    }

    interface FragmentContainer {
        fun onRequestOnTagUpdatedListener(): OnTagUpdatedListener?
    }

    private var typedValue = TypedValue()  // android.R.attr.selectableItemBackgroundBorderless

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    private object Key {
        const val Tag =  "com.grand.duke.elliot.restaurantpost.ui.tag" +
                ".TagEditingDialogFragment.Key.Tag"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(Key.Tag, tag)
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        context.theme.resolveAttribute(
                android.R.attr.selectableItemBackgroundBorderless,
                typedValue,
                true
        )

        if (context is FragmentContainer)
            onTagUpdatedListener = context.onRequestOnTagUpdatedListener()

        super.onAttach(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.WindowAnimations
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_tag_editing_dialog,
                container,
                false
        )

        savedInstanceState?.run {
            tag = savedInstanceState.getParcelable(Key.Tag)
        }

        tag?.run {
            binding.textInputEditText.setText(name)
            binding.textViewTitle.text = getString(R.string.edit_tag)
        } ?: run {
            binding.textViewTitle.text = getString(R.string.create_tag)
        }

        binding.buttonCancel.setTextColor(MainApplication.themePrimaryColor)
        binding.buttonOk.setTextColor(MainApplication.themePrimaryColor)

        binding.buttonOk.setOnClickListener {
            tag?.run {
                val name = binding.textInputEditText.text.toString()

                if (name.isNotBlank()) {
                    tag?.let {
                        it.name = name

                        update(it)
                    }
                } else {
                    binding.textInputLayout.isErrorEnabled = true
                    binding.textInputLayout.error = getString(R.string.enter_tag_name)
                }
            } ?: run {
                createTag()?.let { tag -> insert(tag) } ?: run {
                    binding.textInputLayout.isErrorEnabled = true
                    binding.textInputLayout.error = getString(R.string.enter_tag_name)
                }
            }
        }

        binding.buttonCancel.setOnClickListener {
            dismiss()
        }

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    override fun onDestroyView() {
        compositeDisposable.clear()
        super.onDestroyView()
    }

    private fun insert(tag: Tag) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                compositeDisposable.add(tagDao.insert(tag)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            Timber.d("Tag is inserted: $tag")
                        }, { throwable ->
                            Timber.e(throwable, "Tag insertion failed.")
                        }))
            }

            dismiss()
        }
    }

    private fun update(tag: Tag) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                compositeDisposable.add(tagDao.update(tag)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            onTagUpdatedListener?.onTagUpdated(tag)
                        }, { throwable ->
                            onTagUpdatedListener?.onError(throwable)
                        }))
            }

            dismiss()
        }
    }

    private fun createTag(): Tag? {
        val name = binding.textInputEditText.text.toString()

        if (name.isBlank())
            return null

        return Tag(name = name)
    }
}