package com.grand.duke.elliot.restaurantpost.ui.home

import android.os.Bundle
import android.transition.Explode
import android.view.Window
import com.grand.duke.elliot.restaurantpost.R
import com.grand.duke.elliot.restaurantpost.base.BaseActivity
import com.grand.duke.elliot.restaurantpost.databinding.ActivityMainBinding

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    override val layoutRes: Int
        get() = R.layout.activity_main

    override fun viewModel(): Class<MainViewModel> = MainViewModel::class.java
}