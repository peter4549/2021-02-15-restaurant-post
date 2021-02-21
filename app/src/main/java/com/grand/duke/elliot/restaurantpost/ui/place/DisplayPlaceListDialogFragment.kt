package com.grand.duke.elliot.restaurantpost.ui.place

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.grand.duke.elliot.restaurantpost.repository.LocalRepository
import com.grand.duke.elliot.restaurantpost.repository.data.DisplayPlace
import com.grand.duke.elliot.restaurantpost.ui.util.dialog_fragment.SearchBarListAdapter
import com.grand.duke.elliot.restaurantpost.ui.util.dialog_fragment.SearchBarListDialogFragment
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class DisplayPlaceListDialogFragment: SearchBarListDialogFragment<DisplayPlace>() {

    @Inject
    lateinit var localRepository: LocalRepository

    private val displayPlaceListAdapter = DisplayPlaceListAdapter()
    override val listAdapter: SearchBarListAdapter<DisplayPlace>
        get() = displayPlaceListAdapter

    private val compositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        compositeDisposable.add(localRepository.placeWithPostListFlowable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    displayPlaceListAdapter.submitItemList(it.map { placeWithPostList ->
                        DisplayPlace(
                                placeWithPostList.place.id,
                                placeWithPostList.place.name,
                                placeWithPostList.postList.count()
                        )
                    })
                }, {
                    Timber.e(it)
                }))

        return view
    }

    override fun onDestroyView() {
        compositeDisposable.clear()
        super.onDestroyView()
    }
}