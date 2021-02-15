package com.grand.duke.elliot.restaurantpost.dagger.component

import android.app.Application
import com.grand.duke.elliot.restaurantpost.application.MainApplication
import com.grand.duke.elliot.restaurantpost.dagger.module.*
import com.grand.duke.elliot.restaurantpost.ui.post.WritingActivity
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ApplicationModule::class,
    AndroidInjectionModule::class,
    ActivityBindingModule::class,
    FragmentBindingModule::class,
    ViewModelFactoryModule::class,
    ViewModelModule::class
])
interface ApplicationComponent: AndroidInjector<MainApplication> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): ApplicationComponent
    }

    override fun inject(mainApplication: MainApplication)
}

@Component(modules = [WritingModule::class])
interface WritingComponent{
    fun inject(writingActivity: WritingActivity)
}
