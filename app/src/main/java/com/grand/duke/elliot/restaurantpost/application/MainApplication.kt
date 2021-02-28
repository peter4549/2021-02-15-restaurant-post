package com.grand.duke.elliot.restaurantpost.application

import android.app.Application
import android.graphics.Color
import androidx.annotation.ColorInt
import com.google.android.libraries.places.api.Places
import com.grand.duke.elliot.restaurantpost.R
import com.grand.duke.elliot.restaurantpost.application.shared_preferences.SharedPreferencesHelper
import com.grand.duke.elliot.restaurantpost.dagger.component.DaggerApplicationComponent
import com.grand.duke.elliot.restaurantpost.persistence.AppDatabase
import com.grand.duke.elliot.restaurantpost.persistence.data.Folder
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class MainApplication: Application(), HasAndroidInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    @Inject
    lateinit var appDatabase: AppDatabase

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        DaggerApplicationComponent.builder()
            .application(this)
            .build()
            .inject(this)

        themePrimaryColor = sharedPreferencesHelper.getPrimaryThemeColor()

        if (Places.isInitialized().not())
            Places.initialize(this, getString(R.string.api_key), Locale.getDefault())
    }

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector

    companion object {
        @ColorInt
        var themePrimaryColor = 0
    }
}