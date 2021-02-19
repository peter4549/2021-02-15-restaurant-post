package com.grand.duke.elliot.restaurantpost.ui.post.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.grand.duke.elliot.restaurantpost.R
import com.grand.duke.elliot.restaurantpost.base.BaseFragment
import com.grand.duke.elliot.restaurantpost.databinding.FragmentPostListBinding

class PostListFragment: BaseFragment<PostListViewModel, FragmentPostListBinding>() {

    override val layoutRes: Int
        get() = R.layout.fragment_post_list

    override fun viewModel(): Class<PostListViewModel> = PostListViewModel::class.java

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
            showToast(it.toString())
        })

        return view
    }
}