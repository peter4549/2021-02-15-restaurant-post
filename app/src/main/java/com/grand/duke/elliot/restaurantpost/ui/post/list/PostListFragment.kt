package com.grand.duke.elliot.restaurantpost.ui.post.list

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.transition.Explode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.grand.duke.elliot.restaurantpost.R
import com.grand.duke.elliot.restaurantpost.base.BaseFragment
import com.grand.duke.elliot.restaurantpost.databinding.FragmentPostListBinding
import com.grand.duke.elliot.restaurantpost.databinding.ItemFramePostBinding
import com.grand.duke.elliot.restaurantpost.persistence.data.Post
import com.grand.duke.elliot.restaurantpost.ui.home.MainViewModel
import com.grand.duke.elliot.restaurantpost.ui.post.writing.WritingActivity
import io.reactivex.disposables.CompositeDisposable

class PostListFragment: BaseFragment<MainViewModel, FragmentPostListBinding>() {

    object ExtraName {
        const val Post = "com.grand.duke.elliot.restaurantpost.ui.post.list" +
                ".post_list_fragment.extra_name.post"
    }

    private val compositeDisposable by lazy {
        CompositeDisposable()
    }

    override val layoutRes: Int
        get() = R.layout.fragment_post_list

    override fun viewModel(): Class<MainViewModel> = MainViewModel::class.java

    private val postAdapter = PostAdapter().apply {
        setOnItemClickListener(object: PostAdapter.OnItemClickListener {
            override fun onItemClick(holder: PostAdapter.ViewHolder, post: Post) {
                val intent = Intent(requireActivity(), WritingActivity::class.java)
                intent.putExtra(ExtraName.Post, post)

                val activityOptions = ActivityOptions
                        .makeSceneTransitionAnimation(
                                requireActivity(),
                                (holder.binding as ItemFramePostBinding).relativeLayoutViewPager,
                                "view_pager"
                        ).toBundle()
                startActivity(intent, activityOptions)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        viewDataBinding.recyclerView.apply {
            adapter = postAdapter
            layoutManager = StaggeredGridLayoutManager(
                    2,
                    StaggeredGridLayoutManager.VERTICAL
            )
        }

        /** LiveData. */
        viewModel.postList.observe(viewLifecycleOwner, {
            postAdapter.submitPostList(it, requireContext())
        })

        return view
    }

    override fun onDestroyView() {
        compositeDisposable.clear()
        super.onDestroyView()
    }
}