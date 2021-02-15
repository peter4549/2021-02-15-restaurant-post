package com.grand.duke.elliot.restaurantpost.application

import android.app.Application
import androidx.annotation.ColorInt
import com.grand.duke.elliot.restaurantpost.application.shared_preferences.SharedPreferences
import com.grand.duke.elliot.restaurantpost.dagger.component.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import timber.log.Timber
import javax.inject.Inject

class MainApplication: Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        DaggerApplicationComponent.builder()
            .application(this)
            .build()
            .inject(this)

        themePrimaryColor = sharedPreferences.getPrimaryThemeColor()
    }

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector

    companion object {
        @ColorInt
        var themePrimaryColor = 0
    }
}