package com.grand.duke.elliot.restaurantpost.ui.post.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.grand.duke.elliot.restaurantpost.R
import com.grand.duke.elliot.restaurantpost.base.BaseFragment
import com.grand.duke.elliot.restaurantpost.databinding.FragmentPostListBinding
import com.grand.duke.elliot.restaurantpost.ui.home.MainViewModel
import io.reactivex.disposables.CompositeDisposable

class PostListFragment: BaseFragment<MainViewModel, FragmentPostListBinding>() {

    private val compositeDisposable by lazy {
        CompositeDisposable()
    }

    override val layoutRes: Int
        get() = R.layout.fragment_post_list

    override fun viewModel(): Class<MainViewModel> = MainViewModel::class.java

    private val postAdapter = PostAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        viewDataBinding.recyclerView.apply {
            adapter = postAdapter
            layoutManager = StaggeredGridLayoutManager(
                    2,
                    StaggeredGridLayoutManager.VERTICAL
            )
        }

        viewModel.postList().observe(viewLifecycleOwner, {
            postAdapter.submitDataList(it)
        })

        return view
    }

    override fun onDestroyView() {
        compositeDisposable.clear()
        super.onDestroyView()
    }
}