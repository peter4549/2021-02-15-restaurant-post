package com.grand.duke.elliot.restaurantpost.ui.folder

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.resources.TextAppearance
import com.grand.duke.elliot.restaurantpost.R
import com.grand.duke.elliot.restaurantpost.application.MainApplication
import com.grand.duke.elliot.restaurantpost.databinding.FragmentFolderEditingDialogBinding
import com.grand.duke.elliot.restaurantpost.persistence.dao.FolderDao
import com.grand.duke.elliot.restaurantpost.persistence.data.Folder
import com.grand.duke.elliot.restaurantpost.ui.util.isZero
import com.grand.duke.elliot.restaurantpost.ui.util.toHexColor
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import petrov.kristiyan.colorpicker.ColorPicker
import javax.inject.Inject

class FolderEditingDialogFragment: DialogFragment() {

    @Inject
    lateinit var folderDao: FolderDao

    private lateinit var binding: FragmentFolderEditingDialogBinding

    private var folder: Folder? = null
    private var position: Int = -1
    private var textAppearance: Int = -1

    fun setFolder(folder: Folder) {
        this.folder = folder
    }

    fun setPosition(position: Int) {
        this.position = position
    }

    fun setTextAppearance(@StyleRes textAppearance: Int) {
        this.textAppearance = textAppearance
    }

    private var onFolderUpdatedListener: OnFolderUpdatedListener? = null

    interface OnFolderUpdatedListener {
        fun onFolderUpdated(folder: Folder, position: Int)
        fun onError(throwable: Throwable)
    }

    interface FragmentContainer {
        fun onRequestOnFolderUpdatedListener(): OnFolderUpdatedListener?
    }

    private var color = 0
    private var typedValue = TypedValue()  // android.R.attr.selectableItemBackgroundBorderless

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    private object Key {
        const val Position = "com.grand.duke.elliot.restaurantpost.ui.folder" +
                ".FolderEditingDialogFragment.Key.Position"
        const val Folder =  "com.grand.duke.elliot.restaurantpost.ui.folder" +
                ".FolderEditingDialogFragment.Key.Folder"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(Key.Position, position)
        outState.putParcelable(Key.Folder, folder)
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        context.theme.resolveAttribute(
                android.R.attr.selectableItemBackgroundBorderless,
                typedValue,
                true
        )

        if (context is FragmentContainer)
            onFolderUpdatedListener = context.onRequestOnFolderUpdatedListener()

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
                R.layout.fragment_folder_editing_dialog,
                container,
                false
        )

        savedInstanceState?.run {
            position = savedInstanceState.getInt(Key.Position)
            folder = savedInstanceState.getParcelable(Key.Folder)
        }

        color = folder?.color ?: ContextCompat.getColor(requireContext(), R.color.pink_400)  // Default color.
        folder?.run {
            binding.textInputEditText.setText(name)
            binding.textViewTitle.text = getString(R.string.edit_folder)
        } ?: run {
            binding.textViewTitle.text = getString(R.string.create_folder)
        }

        binding.cardViewFolderColor.setCardBackgroundColor(color)

        binding.textViewOk.setOnClickListener {
            folder?.run {
                val name = binding.textInputEditText.text.toString()

                if (name.isNotBlank()) {
                    folder?.let {
                        it.name = name
                        it.color = color

                        update(it)
                        onFolderUpdatedListener?.onFolderUpdated(it, position)
                    }
                } else {
                    binding.textInputLayout.isErrorEnabled = true
                    binding.textInputLayout.error = getString(R.string.enter_folder_name)
                }
            } ?: run {
                createFolder()?.let { folder -> insert(folder) } ?: run {
                    binding.textInputLayout.isErrorEnabled = true
                    binding.textInputLayout.error = getString(R.string.enter_folder_name)
                }
            }
        }

        binding.textViewCancel.setOnClickListener {
            dismiss()
        }

        binding.linearLayoutSelectFolderColor.setOnClickListener {
            val colorPicker = ColorPicker(requireActivity())
            val themeColors = requireContext().resources.getIntArray(R.array.theme_colors).toList()
            val hexColors = themeColors.map { it.toHexColor() } as ArrayList
            val previousColor = color

            colorPicker.setOnChooseColorListener(object : ColorPicker.OnChooseColorListener {
                override fun onChooseColor(position: Int, color: Int) {
                    if (color.isZero())
                        this@FolderEditingDialogFragment.color = previousColor
                    else
                        this@FolderEditingDialogFragment.color = color

                    binding.cardViewFolderColor.setCardBackgroundColor(this@FolderEditingDialogFragment.color)
                }

                override fun onCancel() {
                    color = previousColor
                }
            }).setTitle(getString(R.string.select_folder_color_title))
                    .setColumns(6)
                    .setColorButtonMargin(2, 2, 2, 2)
                    .setColorButtonDrawable(R.drawable.background_white_rounded_corners)
                    .setColors(hexColors)
                    .setDefaultColorButton(color)
                    .show()

            colorPicker.negativeButton?.let {
                it.setText(R.string.cancel)
                it.setTextColor(MainApplication.themePrimaryColor)
                it.setBackgroundResource(typedValue.resourceId)

                if (textAppearance != -1) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
                        it.setTextAppearance(textAppearance)
                    else
                        @Suppress("DEPRECATION")
                        it.setTextAppearance(it.context, textAppearance)
                }
            }

            colorPicker.positiveButton?.let {
                it.setText(R.string.ok)
                it.setTextColor(MainApplication.themePrimaryColor)
                it.setBackgroundResource(typedValue.resourceId)

                if (textAppearance != -1) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
                        it.setTextAppearance(textAppearance)
                    else
                        @Suppress("DEPRECATION")
                        it.setTextAppearance(it.context, textAppearance)
                }
            }
        }

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    private fun getColor(context: Context, id: Int) = ContextCompat.getColor(context, id)

    private fun insert(folder: Folder) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                folderDao.insert(folder)
            }

            dismiss()
        }
    }

    private fun update(folder: Folder) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                folderDao.update(folder)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        onFolderUpdatedListener?.onFolderUpdated(folder, position)
                    }, { throwable ->
                        onFolderUpdatedListener?.onError(throwable)
                    })
            }

            dismiss()
        }
    }

    private fun createFolder(): Folder? {
        val name = binding.textInputEditText.text.toString()

        if (name.isBlank())
            return null

        return Folder(
                name = name,
                color = color
        )
    }
}