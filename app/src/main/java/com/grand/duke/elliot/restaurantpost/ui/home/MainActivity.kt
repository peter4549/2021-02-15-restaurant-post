package com.grand.duke.elliot.restaurantpost.ui.home

import android.os.Bundle
import android.transition.Explode
import android.transition.Fade
import android.transition.TransitionInflater
import android.view.Window
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import com.grand.duke.elliot.restaurantpost.R
import com.grand.duke.elliot.restaurantpost.base.BaseActivity
import com.grand.duke.elliot.restaurantpost.databinding.ActivityMainBinding
import com.grand.duke.elliot.restaurantpost.repository.data.DisplayFolder
import com.grand.duke.elliot.restaurantpost.repository.data.DisplayPlace
import com.grand.duke.elliot.restaurantpost.repository.data.DisplayTag
import com.grand.duke.elliot.restaurantpost.ui.folder.DisplayFolderListDialogFragment
import com.grand.duke.elliot.restaurantpost.ui.folder.FolderEditingDialogFragment
import com.grand.duke.elliot.restaurantpost.ui.place.DisplayPlaceListDialogFragment
import com.grand.duke.elliot.restaurantpost.ui.place.PlaceEditingDialogFragment
import com.grand.duke.elliot.restaurantpost.ui.tag.DisplayTagListDialogFragment
import com.grand.duke.elliot.restaurantpost.ui.tag.TagEditingDialogFragment
import com.grand.duke.elliot.restaurantpost.ui.util.dialog_fragment.SearchBarListAdapter
import com.grand.duke.elliot.restaurantpost.ui.util.dialog_fragment.SearchBarListDialogFragment

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(){
    override val layoutRes: Int
        get() = R.layout.activity_main

    override fun viewModel(): Class<MainViewModel> = MainViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        window?.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        super.onCreate(savedInstanceState)

        with(window) {
            allowEnterTransitionOverlap = true
            allowReturnTransitionOverlap = true

            val fade = Fade()

            fade.excludeTarget(android.R.id.statusBarBackground, true)
            fade.excludeTarget(android.R.id.navigationBarBackground, true)
            fade.excludeTarget(R.id.appbar_layout, true)
            fade.excludeTarget(R.id.toolbar, true)

            enterTransition = fade
            exitTransition = fade

            val changeImageTransform = TransitionInflater.from(this@MainActivity)
                    .inflateTransition(R.transition.change_image_transform).apply {
                        duration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
                    }

            sharedElementEnterTransition = changeImageTransform
            sharedElementExitTransition = changeImageTransform
        }
    }
}