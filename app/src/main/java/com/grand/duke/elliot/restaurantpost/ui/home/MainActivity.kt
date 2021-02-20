package com.grand.duke.elliot.restaurantpost.ui.home

import android.os.Bundle
import com.grand.duke.elliot.restaurantpost.R
import com.grand.duke.elliot.restaurantpost.base.BaseActivity
import com.grand.duke.elliot.restaurantpost.databinding.ActivityMainBinding

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
                .add(R.id.nav_host_fragment, TabFragment()).commit()
    }

    override val layoutRes: Int
        get() = R.layout.activity_main

    override fun viewModel(): Class<MainViewModel> = MainViewModel::class.java
}