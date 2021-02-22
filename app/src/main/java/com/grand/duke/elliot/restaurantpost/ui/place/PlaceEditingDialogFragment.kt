package com.grand.duke.elliot.restaurantpost.ui.place

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcelable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StyleRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.viewModelScope
import com.grand.duke.elliot.restaurantpost.R
import com.grand.duke.elliot.restaurantpost.application.MainApplication
import com.grand.duke.elliot.restaurantpost.databinding.FragmentPlaceEditingDialogBinding
import com.grand.duke.elliot.restaurantpost.persistence.dao.PlaceDao
import com.grand.duke.elliot.restaurantpost.persistence.data.Place
import com.grand.duke.elliot.restaurantpost.ui.post.location.PlaceContent
import com.grand.duke.elliot.restaurantpost.ui.util.blank
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

class PlaceEditingDialogFragment: DialogFragment() {

    @Inject
    lateinit var placeDao: PlaceDao

    private lateinit var binding: FragmentPlaceEditingDialogBinding

    private val compositeDisposable by lazy {
        CompositeDisposable()
    }

    private var place: Place? = null
    private var placeContent: PlaceContent? = null
    private var textAppearance: Int = -1
    private var title = blank

    fun setPlace(place: Place) {
        this.place = place
    }

    fun setPlaceContent(placeContent: PlaceContent) {
        this.placeContent = placeContent
    }

    fun setTextAppearance(@StyleRes textAppearance: Int) {
        this.textAppearance = textAppearance
    }

    fun setTitle(title: String) {
        this.title = title
    }

    private var onPlaceUpdatedListener: OnPlaceUpdatedListener? = null

    interface OnPlaceUpdatedListener {
        fun onError(throwable: Throwable)
        fun onPlaceInserted(place: Place?)
        fun onPlaceUpdated(place: Place)
    }

    interface FragmentContainer {
        fun onRequestOnPlaceUpdatedListener(): OnPlaceUpdatedListener?
    }

    private var typedValue = TypedValue()  // android.R.attr.selectableItemBackgroundBorderless

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    private lateinit var allPlaceContentList: List<PlaceContent>

    private object Key {
        const val Place =  "com.grand.duke.elliot.restaurantpost.ui.place" +
                ".place_editing_dialog_fragment.key.place"
        const val PlaceContent =  "com.grand.duke.elliot.restaurantpost.ui.place" +
                ".place_editing_dialog_fragment.key.place_content"
        const val Title =  "com.grand.duke.elliot.restaurantpost.ui.place" +
                ".place_editing_dialog_fragment.key.title"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(Key.Place, place)
        outState.putParcelable(Key.PlaceContent, placeContent)
        outState.putString(Key.Title, title)
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        context.theme.resolveAttribute(
                android.R.attr.selectableItemBackgroundBorderless,
                typedValue,
                true
        )

        if (context is FragmentContainer)
            onPlaceUpdatedListener = context.onRequestOnPlaceUpdatedListener()

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                allPlaceContentList = placeDao.getAllPlaceContentList()
            }
        }

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
                R.layout.fragment_place_editing_dialog,
                container,
                false
        )

        savedInstanceState?.run {
            place = savedInstanceState.getParcelable(Key.Place)
            placeContent = savedInstanceState.getParcelable(Key.PlaceContent)
            title = savedInstanceState.getString(Key.Title) ?: getString(R.string.create_place)
        }

        place?.run { binding.textInputEditText.setText(name) }
        placeContent?.run { binding.textInputEditText.setText(name) }

        binding.textViewTitle.text = title

        binding.buttonCancel.setTextColor(MainApplication.themePrimaryColor)
        binding.buttonOk.setTextColor(MainApplication.themePrimaryColor)

        binding.buttonOk.setOnClickListener {
            place?.run {
                val name = binding.textInputEditText.text.toString()

                if (name.isNotBlank()) {
                    place?.let {
                        it.name = name
                        update(it)
                    }
                } else {
                    binding.textInputLayout.isErrorEnabled = true
                    binding.textInputLayout.error = getString(R.string.enter_place_name)
                }
            } ?: run {
                val name = binding.textInputEditText.text.toString()
                val latitude = placeContent?.latitude ?: run {
                    dismiss()
                    return@setOnClickListener
                }
                val longitude = placeContent?.longitude ?: run {
                    dismiss()
                    return@setOnClickListener
                }

                if (name.isBlank()) {
                    binding.textInputLayout.isErrorEnabled = true
                    binding.textInputLayout.error = getString(R.string.enter_place_name)
                    return@setOnClickListener
                }

                val newPlaceContent = PlaceContent(
                        name = name,
                        latitude = latitude,
                        longitude = longitude
                )

                if (allPlaceContentList.contains(newPlaceContent)) {
                    onPlaceUpdatedListener?.onPlaceInserted(null)
                    dismiss()
                } else {
                    val place = createPlace(newPlaceContent)
                    place?.let {
                        insert(it)
                    } ?: run {
                        binding.textInputLayout.isErrorEnabled = true
                        binding.textInputLayout.error = getString(R.string.enter_place_name)
                    }
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

    private fun insert(place: Place) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                compositeDisposable.add(placeDao.insert(place)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            Timber.d("Place is inserted: $place")
                            onPlaceUpdatedListener?.onPlaceInserted(place)
                        }, { throwable ->
                            Timber.e(throwable, "Place insertion failed.")
                        }))
            }

            dismiss()
        }
    }

    private fun update(place: Place) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                compositeDisposable.add(placeDao.update(place)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            onPlaceUpdatedListener?.onPlaceUpdated(place)
                        }, { throwable ->
                            onPlaceUpdatedListener?.onError(throwable)
                        }))
            }

            dismiss()
        }
    }

    private fun createPlace(placeContent: PlaceContent): Place? {
        val name = binding.textInputEditText.text.toString()

        if (name.isBlank())
            return null

        return Place(
                name = name,
                latitude = placeContent.latitude,
                longitude = placeContent.longitude
        )
    }
}