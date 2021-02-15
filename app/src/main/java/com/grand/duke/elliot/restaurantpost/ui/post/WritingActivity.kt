package com.grand.duke.elliot.restaurantpost.ui.post

import android.Manifest
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks
import com.github.ksoichiro.android.observablescrollview.ScrollState
import com.github.ksoichiro.android.observablescrollview.ScrollUtils
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.grand.duke.elliot.restaurantpost.dagger.component.DaggerWritingComponent
import com.grand.duke.elliot.restaurantpost.databinding.ActivityWritingBinding
import com.grand.duke.elliot.restaurantpost.persistence.data.Place
import com.grand.duke.elliot.restaurantpost.R
import com.grand.duke.elliot.restaurantpost.application.MainApplication
import com.grand.duke.elliot.restaurantpost.persistence.data.Folder
import com.grand.duke.elliot.restaurantpost.ui.fluid_content_resize.FluidContentResize
import com.grand.duke.elliot.restaurantpost.ui.folder.DisplayFolderListDialogFragment
import com.grand.duke.elliot.restaurantpost.ui.folder.FolderEditingDialogFragment
import com.grand.duke.elliot.restaurantpost.ui.util.*
import com.grand.duke.elliot.restaurantpost.ui.util.dialog_fragment.SearchBarListAdapter
import com.grand.duke.elliot.restaurantpost.ui.util.dialog_fragment.SearchBarListDialogFragment
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import javax.inject.Inject

class WritingActivity: AppCompatActivity(),
    ObservableScrollViewCallbacks, OnMapReadyCallback,
    SearchBarListDialogFragment.FragmentContainer, FolderEditingDialogFragment.FragmentContainer {

    @Inject
    lateinit var viewModelFactory: WritingViewModel.Factory

    private lateinit var viewModel: WritingViewModel
    private lateinit var binding: ActivityWritingBinding

    private lateinit var location: com.grand.duke.elliot.restaurantpost.ui.post.location.Location
    private var googleMap: GoogleMap? = null
    private var marker: Marker? = null

    private lateinit var uiController: UiController

    private var mediumAnimationDuration = 0
    private var shortAnimationDuration = 0

    private var parallaxImageHeight = 0
    private var toolbarHeight = 0

    private var bottomSheetIsShown = true
    private var bottomSheetHeight = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerWritingComponent.create().inject(this)

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_writing)
        viewModel = viewModelFactory.create(null)
        init()
    }

    override fun onResume() {
        super.onResume()
        requestPermissions()
    }

    private fun init() {
        mediumAnimationDuration = resources.getInteger(android.R.integer.config_mediumAnimTime)
        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

        parallaxImageHeight = resources.getDimensionPixelSize(R.dimen.parallax_image_height)
        toolbarHeight = actionBarSize()

        bottomSheetHeight = convertDpToPx(
            this,
            resources.getDimension(R.dimen.bottom_sheet_height) / resources.displayMetrics.density
        )

        FluidContentResize.listen(this)

        uiController = UiController()
        uiController.apply {
            updateMap()
            setText()
            initBottomSheet()
        }
    }

    private fun requestPermissions() {
        val multiplePermissionListener = object: MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report.deniedPermissionResponses.isEmpty())
                    return

                for (response in report.deniedPermissionResponses) {
                    finish()
                    break
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                token?.run { continuePermissionRequest() }
            }
        }

        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(multiplePermissionListener)
            .check()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap

        location.lastPlace {
            it?.also {
                viewModel.setPlace(it)
            } ?: run {
                viewModel.setPlace(location.defaultPlace())
            }
        }
    }

    private inner class UiController {
        fun updateMap() {
            location = com.grand.duke.elliot.restaurantpost.ui.post.location.Location(this@WritingActivity)
            val supportMapFragment = supportFragmentManager.findFragmentById(R.id.fragment_support_map) as SupportMapFragment
            supportMapFragment.getMapAsync(this@WritingActivity)

            viewModel.place.observe(this@WritingActivity, {
                it?.run {
                    if (binding.chipPlace.isVisible.not())
                        binding.chipPlace.fadeIn(shortAnimationDuration)
                    updateMap(it)
                } ?: run {
                    if (binding.chipPlace.isVisible)
                        binding.chipPlace.fadeOut(shortAnimationDuration)
                }
            })
        }

        fun setText() {
            binding.textViewDate.text = viewModel.modifiedTime.toSimpleDateFormat(getString(R.string.pattern_date))
            binding.textViewTime.text = viewModel.modifiedTime.toSimpleDateFormat(getString(R.string.pattern_time))
        }

        fun initBottomSheet() {
            binding.frameLayoutArrowDropDown.setOnClickListener {
                bottomSheetIsShown = when {
                    bottomSheetIsShown -> {
                        binding.imageViewArrowDropDown.rotate(180F, shortAnimationDuration)
                        binding.linearLayoutBottomSheet.hideDown(shortAnimationDuration, bottomSheetHeight)
                        false
                    }
                    else -> {
                        binding.imageViewArrowDropDown.rotate(0F, shortAnimationDuration)
                        binding.linearLayoutBottomSheet.showUp(shortAnimationDuration, bottomSheetHeight)
                        true
                    }
                }
            }

            binding.imageViewFolder.setOnClickListener {
                val title = getString(R.string.folder)
                DisplayFolderListDialogFragment().apply{
                    setTitle(title)
                }.show(supportFragmentManager, null)
            }
        }
    }

    private fun updateMap(place: Place) {
        val name = place.name
        val latLng = LatLng(place.latitude, place.longitude)

        marker?.remove()
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title(name)
        markerOptions.draggable(true)
        marker = googleMap?.addMarker(markerOptions)

        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15F)
        googleMap?.moveCamera(cameraUpdate)
    }

    /** ObservableScrollViewCallbacks. */
    override fun onScrollChanged(scrollY: Int, firstScroll: Boolean, dragging: Boolean) {
        val alpha = 1F.coerceAtMost(scrollY / (parallaxImageHeight - toolbarHeight).toFloat())

        if (binding.relativeLayoutViewPager.isVisible)
            binding.toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, MainApplication.themePrimaryColor))
        else
            binding.toolbar.setBackgroundColor(MainApplication.themePrimaryColor)

        if (binding.relativeLayoutViewPager.isVisible)
            binding.relativeLayoutViewPager.translationY = scrollY / 2F
    }

    override fun onDownMotionEvent() {}
    override fun onUpOrCancelMotionEvent(scrollState: ScrollState?) {}

    private fun actionBarSize(): Int {
        val typedValue = TypedValue()
        var actionBarSize = 0

        if (theme.resolveAttribute(android.R.attr.actionBarSize, typedValue, true))
            actionBarSize = TypedValue.complexToDimensionPixelSize(typedValue.data, resources.displayMetrics)

        return actionBarSize
    }

    /** SearchBarListDialogFragment.FragmentContainer. */
    override fun onRequestOnClickListener(): SearchBarListDialogFragment.OnClickListener? =
        object: SearchBarListDialogFragment.OnClickListener {
            override fun onAddClick() {
                FolderEditingDialogFragment().run { show(supportFragmentManager, tag) }
            }
        }

    override fun <T> onRequestOnItemClickListener(): SearchBarListAdapter.OnItemClickListener<T> =
        object: SearchBarListAdapter.OnItemClickListener<T> {
            override fun onItemClick(item: T, adapterPosition: Int) {

            }

            override fun onMoreClick(item: T, adapterPosition: Int) {

            }
        }

    /** FolderEditingDialogFragment.FragmentContainer. */
    override fun onRequestOnFolderUpdatedListener(): FolderEditingDialogFragment.OnFolderUpdatedListener =
        object: FolderEditingDialogFragment.OnFolderUpdatedListener {
            override fun onFolderUpdated(folder: Folder, position: Int) {

            }

            override fun onError(throwable: Throwable) {

            }
        }
}