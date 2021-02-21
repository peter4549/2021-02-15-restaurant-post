package com.grand.duke.elliot.restaurantpost.ui.post.writing

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Size
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.WindowInsets
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks
import com.github.ksoichiro.android.observablescrollview.ScrollState
import com.github.ksoichiro.android.observablescrollview.ScrollUtils
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.grand.duke.elliot.restaurantpost.R
import com.grand.duke.elliot.restaurantpost.application.MainApplication
import com.grand.duke.elliot.restaurantpost.application.noFolderSelected
import com.grand.duke.elliot.restaurantpost.application.noPlaceSelected
import com.grand.duke.elliot.restaurantpost.databinding.ActivityWritingBinding
import com.grand.duke.elliot.restaurantpost.persistence.data.Folder
import com.grand.duke.elliot.restaurantpost.persistence.data.Place
import com.grand.duke.elliot.restaurantpost.persistence.data.Post
import com.grand.duke.elliot.restaurantpost.persistence.data.Tag
import com.grand.duke.elliot.restaurantpost.repository.LocalRepository
import com.grand.duke.elliot.restaurantpost.repository.data.DisplayFolder
import com.grand.duke.elliot.restaurantpost.repository.data.DisplayTag
import com.grand.duke.elliot.restaurantpost.ui.fluid_content_resize.FluidContentResize
import com.grand.duke.elliot.restaurantpost.ui.folder.DisplayFolderListDialogFragment
import com.grand.duke.elliot.restaurantpost.ui.folder.FolderEditingDialogFragment
import com.grand.duke.elliot.restaurantpost.ui.google_maps.GoogleMapsActivity
import com.grand.duke.elliot.restaurantpost.ui.place.DisplayPlaceListDialogFragment
import com.grand.duke.elliot.restaurantpost.ui.post.photo.PhotoHelper
import com.grand.duke.elliot.restaurantpost.ui.post.photo.PhotoUriStringAdapter
import com.grand.duke.elliot.restaurantpost.ui.tag.DisplayTagListDialogFragment
import com.grand.duke.elliot.restaurantpost.ui.tag.TagEditingDialogFragment
import com.grand.duke.elliot.restaurantpost.ui.util.*
import com.grand.duke.elliot.restaurantpost.ui.util.dialog_fragment.SearchBarListAdapter
import com.grand.duke.elliot.restaurantpost.ui.util.dialog_fragment.SearchBarListDialogFragment
import com.grand.duke.elliot.restaurantpost.ui.util.dialog_fragment.SimpleItem
import com.grand.duke.elliot.restaurantpost.ui.util.dialog_fragment.SimpleListDialogFragment
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.android.AndroidInjection
import timber.log.Timber
import java.lang.NullPointerException
import javax.inject.Inject

class WritingActivity: AppCompatActivity(),
    ObservableScrollViewCallbacks, OnMapReadyCallback,
    SearchBarListDialogFragment.FragmentContainer,
    FolderEditingDialogFragment.FragmentContainer, TagEditingDialogFragment.FragmentContainer,
    SimpleListDialogFragment.FragmentContainer {

    @Inject
    lateinit var viewModelFactory: WritingViewModel.Factory

    @Inject
    lateinit var localRepository: LocalRepository

    private lateinit var viewModel: WritingViewModel
    private lateinit var binding: ActivityWritingBinding
    private lateinit var photoUriStringAdapter: PhotoUriStringAdapter

    private val menuRes = R.menu.menu_writing_activity

    private var googleMap: GoogleMap? = null
    private var marker: Marker? = null

    private lateinit var uiController: UiController

    private var mediumAnimationDuration = 0
    private var shortAnimationDuration = 0

    private var parallaxImageHeight = 0
    private var toolbarHeight = 0

    private var bottomSheetIsShown = true
    private var bottomSheetHeight = 0F

    private var chipFolder: Chip? = null

    private fun isCreationMode() = viewModel.mode() == WritingViewModel.Mode.Creation

    object SimpleItemId {
        const val ImageCapture = "com.grand.duke.elliot.restaurantpost.ui.post" +
                ".simple_item_id.image_capture"
        const val ImagePick = "com.grand.duke.elliot.restaurantpost.ui.post" +
                ".simple_item_id.image_pick"
    }

    private object RequestCode {
        const val GoogleMapsActivity = 13
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_writing)
        viewModel = viewModelFactory.create(null, localRepository)
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
        uiController.init()

        initLiveData()
        setFrameLayoutSpaceHeight()
    }

    private fun initLiveData() {
        viewModel.photoUriStringList.observe(this, { photoUriStringList ->
            if (this::photoUriStringAdapter.isInitialized.not())
                uiController.initViewPager()

            if (photoUriStringList.isEmpty()) {
                binding.relativeLayoutViewPager.hide()

                if (binding.viewAnchor.isVisible.not())
                    binding.viewAnchor.show()
            } else {
                if (binding.relativeLayoutViewPager.isVisible.not())
                    binding.relativeLayoutViewPager.show()

                if (binding.viewAnchor.isVisible)
                    binding.viewAnchor.hide()
            }

            photoUriStringAdapter.submitList(photoUriStringList)
            photoUriStringAdapter.notifyDataSetChanged()
        })

        viewModel.folder.observe(this, { folder ->
            folder?.let {
                if (chipFolder.isNotNull()) {
                    chipFolder?.text = folder.name
                    chipFolder?.invalidate()
                } else {
                    chipFolder = Chip(this)
                    chipFolder?.text = folder.name
                    chipFolder?.chipIcon = ContextCompat.getDrawable(this, R.drawable.ic_round_folder_24)
                    chipFolder?.chipIconSize = convertDpToPx(this, 24F)

                    chipFolder?.setChipIconTintResource(R.color.color_icon)
                    chipFolder?.setOnCloseIconClickListener {
                        viewModel.setFolder(null)
                    }

                    binding.chipGroup.addView(chipFolder, 0)
                }
            } ?: run {
                if (chipFolder.isNotNull()) {
                    binding.chipGroup.removeView(chipFolder)
                    chipFolder = null
                }
            }
        })

        viewModel.displayTagList.observe(this, { displayTagList ->
            displayTagList ?: return@observe
            val changedTag = displayTagList.changedTag
            val changeType = displayTagList.changeType
            val tagList = displayTagList.tagList

            when(changeType) {
                DisplayTagList.ChangeType.Initialized -> {
                    for (tag in tagList) {
                        val chip = Chip(this).apply {
                            this.tag = tag.id
                        }
                        chip.text = tag.name
                        chip.setOnCloseIconClickListener {
                            binding.chipGroup.removeView(it)
                            viewModel.removeTag(tag)
                        }

                        binding.chipGroup.addView(chip)
                    }
                }
                DisplayTagList.ChangeType.Add -> {
                    val tag = changedTag ?: return@observe
                    val chip = Chip(this).apply {
                        this.tag = tag.id
                        text = tag.name
                        setOnCloseIconClickListener {
                            binding.chipGroup.removeView(it)
                            viewModel.removeTag(tag)
                        }
                    }
                    binding.chipGroup.addView(chip)
                }
                DisplayTagList.ChangeType.Remove -> {
                    val tag = changedTag ?: return@observe
                    val chip = binding.chipGroup.findViewWithTag<Chip>(tag.id) ?: return@observe
                    binding.chipGroup.removeView(chip)
                }
                DisplayTagList.ChangeType.Update -> {
                    val tag = changedTag ?: return@observe
                    val chip = binding.chipGroup.findViewWithTag<Chip>(tag.id) ?: return@observe
                    chip.text = tag.name
                    chip.invalidate()
                }
            }
        })

        /** Place. */
        viewModel.place.observe(this@WritingActivity, {
            it?.run {
                //binding.linearLayoutPlace.fadeIn(shortAnimationDuration)
                if (binding.chipPlace.isVisible.not())
                    binding.chipPlace.fadeIn(shortAnimationDuration)
                updateMap(it)
            } ?: run {

                //binding.linearLayoutPlace.fadeOut(shortAnimationDuration)
                //if (binding.chipPlace.isVisible) todo. test.
                //  binding.chipPlace.fadeOut(shortAnimationDuration)
            }
        })
    }

    private fun requestPermissions() {
        val multiplePermissionListener = object: MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report.deniedPermissionResponses.isEmpty()) {
                    // todo something.
                    return
                }

                for (response in report.deniedPermissionResponses) {
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
        uiController.initMap()
    }

    private inner class UiController {

        fun init() {
            setText()
            initBottomSheet()
            initObservableScrollView()
            initViewPager()
            initToolbar()
        }

        fun initMap() {
            val supportMapFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view_support_map) as SupportMapFragment
            supportMapFragment.getMapAsync(this@WritingActivity)
        }

        private fun initObservableScrollView() {
            binding.observableScrollView.setScrollViewCallbacks(this@WritingActivity)
        }

        fun initViewPager() {
            photoUriStringAdapter = PhotoUriStringAdapter()
            photoUriStringAdapter.setOnItemClickListener(object :
                PhotoUriStringAdapter.OnItemClickListener {
                override fun onClick(uriString: String) {
                    // TODO: implement. goto photo view. (fragment.)
                }
            })

            binding.viewPager.adapter = photoUriStringAdapter
            binding.wormDotsIndicator.setViewPager2(binding.viewPager)
        }

        fun setText() {
            binding.textViewDate.text = viewModel.modifiedTime.toSimpleDateFormat(getString(R.string.pattern_date))
            binding.textViewTime.text = viewModel.modifiedTime.toSimpleDateFormat(getString(R.string.pattern_time))
        }

        private fun initBottomSheet() {
            binding.frameLayoutArrowDropDown.setOnClickListener {
                bottomSheetIsShown = when {
                    bottomSheetIsShown -> {
                        binding.imageViewArrowDropDown.rotate(180F, shortAnimationDuration)
                        binding.linearLayoutBottomSheet.hideDown(
                            shortAnimationDuration,
                            bottomSheetHeight
                        )
                        false
                    }
                    else -> {
                        binding.imageViewArrowDropDown.rotate(0F, shortAnimationDuration)
                        binding.linearLayoutBottomSheet.showUp(
                            shortAnimationDuration,
                            bottomSheetHeight
                        )
                        true
                    }
                }
            }

            binding.imageViewLocation.setOnClickListener {
                val title = getString(R.string.place)
                DisplayPlaceListDialogFragment().apply{
                    setTitle(title)
                    show(supportFragmentManager, tag)
                }
            }

            binding.imageViewFolder.setOnClickListener {
                val title = getString(R.string.folder)
                DisplayFolderListDialogFragment().apply{
                    setTitle(title)
                    show(supportFragmentManager, tag)
                }
            }

            binding.imageViewTag.setOnClickListener {
                val title = getString(R.string.tag)
                DisplayTagListDialogFragment().apply {
                    setTitle(title)
                    show(supportFragmentManager, tag)
                }
            }

            binding.imageViewPhoto.setOnClickListener {
                val simpleListDialogFragment = SimpleListDialogFragment()
                simpleListDialogFragment.setTitle(getString(R.string.photo))
                simpleListDialogFragment.setItems(
                    arrayListOf(
                        SimpleItem(
                            SimpleItemId.ImageCapture,
                            getString(R.string.photo_shoot),
                            ContextCompat.getDrawable(
                                this@WritingActivity,
                                R.drawable.ic_round_photo_camera_24
                            )
                        ),
                        SimpleItem(
                            SimpleItemId.ImagePick,
                            getString(R.string.select_from_album),
                            ContextCompat.getDrawable(
                                this@WritingActivity,
                                R.drawable.ic_round_insert_photo_24
                            )
                        )
                    )
                )

                simpleListDialogFragment.run { show(supportFragmentManager, tag) }
            }
        }

        private fun initToolbar() {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when(requestCode) {
                PhotoHelper.RequestCode.ImageCapture -> {
                    PhotoHelper.getPhotoUri()?.let {
                        viewModel.addPhotoUri(it)
                    }
                }
                PhotoHelper.RequestCode.ImagePicker -> {
                    data?.data?.let { uri ->
                        viewModel.addPhotoUri(uri)
                    } ?: run {
                        Timber.e(NullPointerException("uri is null."))
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        menuInflater.inflate(menuRes, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (isChanged())
                    showSaveConfirmationDialog()
                else
                    finish()
                true
            }
            R.id.item_save -> {
                if (isChanged()) {
                    when (viewModel.mode()) {
                        WritingViewModel.Mode.Creation -> finishWithSave()
                        WritingViewModel.Mode.Edit -> finishWithUpdate()
                    }
                } else
                    finish()
                true
            }
            else -> false
        }
    }

    private fun setFrameLayoutSpaceHeight() {
        binding.constraintLayoutContent.post {
            run {
                binding.viewAnchor.post {
                    val height = screenHeight() - binding.constraintLayoutContent.height

                    binding.frameLayoutSpace.post {
                        val params = binding.frameLayoutSpace.layoutParams
                        params.height = height
                        binding.frameLayoutSpace.layoutParams = params
                    }
                }
            }
        }
    }

    private fun screenHeight(): Int {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val metrics = windowManager.currentWindowMetrics
            val windowInsets = metrics.windowInsets
            val insets = windowInsets.getInsetsIgnoringVisibility(
                WindowInsets.Type.navigationBars()
                        or WindowInsets.Type.displayCutout()
            )

            val insetsWidth: Int = insets.right + insets.left
            val insetsHeight: Int = insets.top + insets.bottom

            val bounds = metrics.bounds
            val size = Size(
                bounds.width() - insetsWidth,
                bounds.height() - insetsHeight
            )

            return size.height
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            val display = windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(displayMetrics)
            return displayMetrics.heightPixels
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

    private fun isChanged(): Boolean {
        when(viewModel.mode()) {
            WritingViewModel.Mode.Creation -> {
                if (viewModel.photoUriList().isNotEmpty())
                    return true

                if (binding.editTextDescription.text.toString().isNotBlank())
                    return true

                if (viewModel.folder() != null)
                    return true

                if (viewModel.tagList().isNotEmpty())
                    return true

                return false
            }
            WritingViewModel.Mode.Edit -> {
                viewModel.post()?.let {
                    if (viewModel.existingPhotoUriArray().contentEquals(viewModel.photoUriList()?.toTypedArray()).not())
                        return true

                    if (it.description != binding.editTextDescription.text.toString())
                        return true

                    if (viewModel.existingFolderId() != viewModel.folder()?.id)
                        return true

                    if (viewModel.existingPlaceId() != viewModel.place()?.id)
                        return true

                    if (viewModel.existingTagArray().contentEquals(viewModel.tagList().toTypedArray()).not())
                        return true

                    return false
                } ?: return false
            }
            else -> return false
        }
    }

    private fun createPost(): Post {
        return Post(
            description = binding.editTextDescription.text.toString(),
            folderId = viewModel.folder()?.id ?: noFolderSelected,
            modifiedTime = viewModel.modifiedTime,
            photoUriStringArray = viewModel.photoUriList().toTypedArray(),
            placeId = viewModel.place()?.id ?: noPlaceSelected
        )
    }

    private fun finishWithSave() {
        val post = createPost()
        val text = getString(R.string.failed_to_save_post)
        viewModel.insertPost(post) { throwable ->
            throwable?.let { showToast(text) }
            finish()
        }
    }

    private fun finishWithUpdate() {
        val post = viewModel.post()
        val text = getString(R.string.post_update_failed)
        post?.let {
            viewModel.updatePost(it) { throwable ->
                throwable?.let { showToast(text) }
                finish()
            }
        }
    }

    override fun onBackPressed() {
        if (isChanged())
            showSaveConfirmationDialog()
        else
            finish()
    }

    /** ObservableScrollViewCallbacks. */
    override fun onScrollChanged(scrollY: Int, firstScroll: Boolean, dragging: Boolean) {
        val alpha = 1F.coerceAtMost(scrollY / (parallaxImageHeight - toolbarHeight).toFloat())

        if (binding.relativeLayoutViewPager.isVisible)
            binding.toolbar.setBackgroundColor(
                ScrollUtils.getColorWithAlpha(
                    alpha,
                    MainApplication.themePrimaryColor
                )
            )
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
            actionBarSize = TypedValue.complexToDimensionPixelSize(
                typedValue.data,
                resources.displayMetrics
            )

        return actionBarSize
    }

    private fun showToast(text: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(this, text, duration).show()
    }

    private fun showSaveConfirmationDialog() {
        val title =
            if (viewModel.mode() == WritingViewModel.Mode.Creation)
                getString(R.string.save_confirmation_dialog_title)
            else
                getString(R.string.change_confirmation_dialog_title)
        val message =
            if (viewModel.mode() == WritingViewModel.Mode.Creation)
                getString(R.string.save_confirmation_dialog_message)
            else
                getString(R.string.change_confirmation_dialog_message)

        showMaterialAlertDialog(
            title = title,
            message = message,
            neutralButtonText = getString(R.string.cancel),
            neutralButtonClickListener = { dialogInterface, _ ->
                dialogInterface?.dismiss()
            },
            negativeButtonText = getString(R.string.do_not_save),
            negativeButtonClickListener = { dialogInterface, _ ->
                finish()
                dialogInterface?.dismiss()
            },
            positiveButtonText = getString(R.string.save),
            positiveButtonClickListener = { dialogInterface, _ ->
                if (viewModel.mode() == WritingViewModel.Mode.Creation)
                    this.finishWithSave()
                else
                    finishWithUpdate()

                dialogInterface?.dismiss()
            }
        )
    }

    private fun showMaterialAlertDialog(
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

    private fun startGoogleMapsActivity() {
        val intent = Intent(this, GoogleMapsActivity::class.java)
        startActivityForResult(intent, RequestCode.GoogleMapsActivity)
    }

    /** SearchBarListDialogFragment.FragmentContainer. */
    override fun onRequestOnClickListener(): SearchBarListDialogFragment.OnClickListener =
        object: SearchBarListDialogFragment.OnClickListener {
            override fun onAddClick(dialogFragment: DialogFragment) {
                when (dialogFragment) {
                    is DisplayFolderListDialogFragment ->
                        FolderEditingDialogFragment().run { show(supportFragmentManager, tag) }
                    is DisplayTagListDialogFragment ->
                        TagEditingDialogFragment().run { show(supportFragmentManager, tag) }
                    is DisplayPlaceListDialogFragment -> { startGoogleMapsActivity() }
                }
            }
        }

    override fun <T> onRequestOnItemClickListener(): SearchBarListAdapter.OnItemClickListener<T> =
        object: SearchBarListAdapter.OnItemClickListener<T> {
            override fun onItemClick(item: T, adapterPosition: Int) {
                when(item) {
                    is DisplayFolder -> {
                        viewModel.setFolder(item.folder)
                    }
                    is DisplayTag -> {
                        viewModel.addTag(item.tag)
                    }
                }
            }

            override fun onDeleteClick(item: T) {
                when(item) {
                    is DisplayFolder -> viewModel.deleteFolder(item.folder)
                    is DisplayTag -> viewModel.deleteTag(item.tag)
                }
            }

            override fun onEditClick(item: T) {
                when(item) {
                    is DisplayFolder -> {
                        FolderEditingDialogFragment().apply {
                            setFolder(item.folder.deepCopy())
                            show(supportFragmentManager, tag)
                        }
                    }
                    is DisplayTag -> {
                        TagEditingDialogFragment().apply {
                            setTag(item.tag.deepCopy())
                            show(supportFragmentManager, tag)
                        }
                    }
                }
            }
        }

    /** FolderEditingDialogFragment.FragmentContainer. */
    override fun onRequestOnFolderUpdatedListener(): FolderEditingDialogFragment.OnFolderUpdatedListener =
        object: FolderEditingDialogFragment.OnFolderUpdatedListener {
            override fun onFolderUpdated(folder: Folder) {
                viewModel.setFolder(folder)
            }

            override fun onError(throwable: Throwable) {
                Timber.e(throwable, "Folder update failed.")
            }
        }

    /** TagEditingDialogFragment.FragmentContainer. */
    override fun onRequestOnTagUpdatedListener(): TagEditingDialogFragment.OnTagUpdatedListener =
        object: TagEditingDialogFragment.OnTagUpdatedListener {
            override fun onTagUpdated(tag: Tag) {
                viewModel.updateTag(tag)
            }

            override fun onError(throwable: Throwable) {
                Timber.e(throwable, "Tag update failed.")
            }
        }

    override fun onRequestOnItemSelectedListener(): SimpleListDialogFragment.OnItemSelectedListener = object: SimpleListDialogFragment.OnItemSelectedListener {
        override fun onItemSelected(dialogFragment: DialogFragment, simpleItem: SimpleItem) {
            when(simpleItem.id) {
                SimpleItemId.ImageCapture -> PhotoHelper.dispatchImageCaptureIntent(this@WritingActivity)
                SimpleItemId.ImagePick -> PhotoHelper.dispatchImagePickerIntent(this@WritingActivity)
            }

            dialogFragment.dismiss()
        }
    }

    override fun onRequestOnScrollReachedBottom(): SimpleListDialogFragment.OnScrollReachedBottomListener? = null
}