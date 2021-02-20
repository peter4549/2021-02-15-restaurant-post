package com.grand.duke.elliot.restaurantpost.dagger.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.grand.duke.elliot.restaurantpost.dagger.ActivityScope
import com.grand.duke.elliot.restaurantpost.dagger.FragmentScope
import com.grand.duke.elliot.restaurantpost.persistence.AppDatabase
import com.grand.duke.elliot.restaurantpost.persistence.dao.*
import com.grand.duke.elliot.restaurantpost.repository.LocalRepository
import com.grand.duke.elliot.restaurantpost.ui.ViewModelFactory
import com.grand.duke.elliot.restaurantpost.ui.calendar.CalendarFragment
import com.grand.duke.elliot.restaurantpost.ui.calendar.CalendarViewModel
import com.grand.duke.elliot.restaurantpost.ui.folder.DisplayFolderListDialogFragment
import com.grand.duke.elliot.restaurantpost.ui.folder.FolderEditingDialogFragment
import com.grand.duke.elliot.restaurantpost.ui.home.MainActivity
import com.grand.duke.elliot.restaurantpost.ui.home.MainViewModel
import com.grand.duke.elliot.restaurantpost.ui.home.TabFragment
import com.grand.duke.elliot.restaurantpost.ui.post.list.PostListFragment
import com.grand.duke.elliot.restaurantpost.ui.post.writing.WritingActivity
import com.grand.duke.elliot.restaurantpost.ui.tag.DisplayTagListDialogFragment
import com.grand.duke.elliot.restaurantpost.ui.tag.TagEditingDialogFragment
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.*
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import javax.inject.Singleton
import kotlin.reflect.KClass

@Module
class ApplicationModule {
    @Provides
    @Singleton
    internal fun provideContext(application: Application): Context {
        return application
    }

    @Provides
    @Singleton
    internal fun provideAppDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, AppDatabase.name).build()
    }

    @Provides
    @Singleton
    internal fun provideFolderDao(appDatabase: AppDatabase): FolderDao {
        return appDatabase.folderDao()
    }

    @Provides
    @Singleton
    internal fun providePlaceDao(appDatabase: AppDatabase): PlaceDao {
        return appDatabase.placeDao()
    }

    @Provides
    @Singleton
    internal fun providePostDao(appDatabase: AppDatabase): PostDao {
        return appDatabase.postDao()
    }

    @Provides
    @Singleton
    internal fun provideTagDao(appDatabase: AppDatabase): TagDao {
        return appDatabase.tagDao()
    }

    @Provides
    @Singleton
    internal fun provideTagPostListDao(appDatabase: AppDatabase): PostTagCrossRefDao {
        return appDatabase.postTagCrossRefDao()
    }

    @Provides
    @Singleton
    internal fun provideLocalRepository(
            folderDao: FolderDao,
            placeDao: PlaceDao,
            postDao: PostDao,
            tagDao: TagDao,
            postTagCrossRefDao: PostTagCrossRefDao
    ): LocalRepository {
        return LocalRepository(folderDao, placeDao, postDao, tagDao, postTagCrossRefDao)
    }


    @Provides
    @Singleton
    internal fun provideSharedPreferences(sharedPreferences: SharedPreferences): SharedPreferences {
        return sharedPreferences
    }
}

@Module
abstract class ActivityBindingModule {
    @ActivityScope
    @ContributesAndroidInjector
    abstract fun mainActivity(): MainActivity
}

@Module
abstract class FragmentBindingModule {
    @FragmentScope
    @ContributesAndroidInjector
    abstract fun displayFolderListDialogFragment(): DisplayFolderListDialogFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun folderEditingDialogFragment(): FolderEditingDialogFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun displayTagListDialogFragment(): DisplayTagListDialogFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun tagEditingDialogFragment(): TagEditingDialogFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun postListFragment(): PostListFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun calendarFragment(): CalendarFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun tabFragment(): TabFragment
}

@Module abstract class ViewModelFactoryModule {
    @Binds
    internal abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory) : ViewModelProvider.Factory
}

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CalendarViewModel::class)
    internal abstract fun bindCalendarViewModel(calendarViewModel: CalendarViewModel): ViewModel
}

@AssistedModule
@Module(includes = [AssistedInject_WritingModule::class])
abstract class WritingModule {
    @ActivityScope
    @ContributesAndroidInjector
    abstract fun writingActivity(): WritingActivity
}

@MustBeDocumented
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)
