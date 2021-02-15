package com.grand.duke.elliot.restaurantpost.ui.tag

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.grand.duke.elliot.restaurantpost.repository.LocalRepository
import com.grand.duke.elliot.restaurantpost.repository.data.DisplayTag
import com.grand.duke.elliot.restaurantpost.ui.util.dialog_fragment.SearchBarListAdapter
import com.grand.duke.elliot.restaurantpost.ui.util.dialog_fragment.SearchBarListDialogFragment
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class DisplayTagListDialogFragment: SearchBarListDialogFragment<DisplayTag>() {

    @Inject
    lateinit var localRepository: LocalRepository

    private val displayTagListAdapter = DisplayTagListAdapter()
    override val listAdapter: SearchBarListAdapter<DisplayTag>
        get() = displayTagListAdapter

    private val compositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        compositeDisposable.add(localRepository.tagWithPostListFlowable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    println("HHHHHHH: ${it.map { a -> a.tag }}")
                    displayTagListAdapter.submitItemList(it.map { tagWithPostList ->
                        DisplayTag(tagWithPostList.tag, tagWithPostList.postList.count())
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