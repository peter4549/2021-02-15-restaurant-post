package com.grand.duke.elliot.restaurantpost.dagger.module

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.grand.duke.elliot.restaurantpost.dagger.FragmentScope
import com.grand.duke.elliot.restaurantpost.persistence.AppDatabase
import com.grand.duke.elliot.restaurantpost.persistence.dao.FolderDao
import com.grand.duke.elliot.restaurantpost.persistence.dao.PlaceDao
import com.grand.duke.elliot.restaurantpost.persistence.dao.PostDao
import com.grand.duke.elliot.restaurantpost.persistence.dao.TagDao
import com.grand.duke.elliot.restaurantpost.repository.LocalRepository
import com.grand.duke.elliot.restaurantpost.ui.ViewModelFactory
import com.grand.duke.elliot.restaurantpost.ui.folder.DisplayFolderListDialogFragment
import com.grand.duke.elliot.restaurantpost.ui.folder.FolderEditingDialogFragment
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.*
import dagger.android.ContributesAndroidInjector
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
    internal fun provideLocalRepository(
        folderDao: FolderDao,
        placeDao: PlaceDao,
        postDao: PostDao,
        tagDao: TagDao
    ): LocalRepository {
        return LocalRepository(folderDao, placeDao, postDao, tagDao)
    }

    /*
    @Provides
    @Singleton
    internal fun provideSharedPreferences(sharedPreferences: SharedPreferences): SharedPreferences {
        return sharedPreferences
    }
     */
}

@Module
abstract class ActivityBindingModule {
    /*
    @ActivityScope
    @ContributesAndroidInjector
    abstract fun writingActivity(): WritingActivity
     */
}

@Module
abstract class FragmentBindingModule {
    @FragmentScope
    @ContributesAndroidInjector
    abstract fun displayFolderListDialogFragment(): DisplayFolderListDialogFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun folderEditingDialogFragment(): FolderEditingDialogFragment
}

@Module abstract class ViewModelFactoryModule {
    @Binds
    internal abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory) : ViewModelProvider.Factory
}

@Module
abstract class ViewModelModule {
    /*
    @Binds
    @IntoMap
    @ViewModelKey(WritingViewModel::class)
    internal abstract fun bindWritingViewModel(writingViewModel: WritingViewModel): ViewModel
     */
}

@AssistedModule
@Module(includes = [AssistedInject_WritingModule::class])
class WritingModule

@MustBeDocumented
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)
