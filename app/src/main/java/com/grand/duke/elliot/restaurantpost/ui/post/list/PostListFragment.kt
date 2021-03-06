package com.grand.duke.elliot.restaurantpost.ui.post.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.chip.Chip
import com.grand.duke.elliot.restaurantpost.R
import com.grand.duke.elliot.restaurantpost.base.BaseFragment
import com.grand.duke.elliot.restaurantpost.databinding.FragmentPostListBinding
import com.grand.duke.elliot.restaurantpost.persistence.data.Post
import com.grand.duke.elliot.restaurantpost.ui.home.MainViewModel
import com.grand.duke.elliot.restaurantpost.ui.home.PostAndViewHolder
import io.reactivex.disposables.CompositeDisposable

class PostListFragment: BaseFragment<MainViewModel, FragmentPostListBinding>() {

    private var isFolderSelected = false
    private var isPlaceSelected = false

    private var folderChip: Chip? = null
    private var placeChip: Chip? = null

    private val tagChipList = mutableListOf<Chip>()

    override val useSharedViewModel: Boolean
        get() = true

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
                viewModel.setClickedPostAndViewHolder(PostAndViewHolder(post, holder))
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
        initLiveData()

        return view
    }

    private fun initLiveData() {
        viewModel.postList.observe(viewLifecycleOwner, {
            it?.let {
                postAdapter.submitPostList(viewModel.filterPostList(it), requireContext())
            }
        })

        viewModel.selectedFolder.observe(viewLifecycleOwner, { folder ->
            folder?.let {
                folderChip = Chip(requireContext()).apply {
                    tag = folder.id
                    text = folder.name
                    setOnCloseIconClickListener {
                        viewModel.setSelectedFolder(null)
                    }
                }

                viewDataBinding.chipGroup.addView(folderChip, 0)
                isFolderSelected = true
            } ?: run {
                folderChip?.let {
                    viewDataBinding.chipGroup.removeView(it)
                    isFolderSelected = false
                }
            }
        })

        viewModel.selectedPlace.observe(viewLifecycleOwner, { place ->
            place?.let {
                placeChip = Chip(requireContext()).apply {
                    tag = place.id
                    text = place.name
                    setOnCloseIconClickListener {
                        viewModel.setSelectedPlace(null)
                    }
                }

                var index = 0

                if (isFolderSelected && viewDataBinding.chipGroup.childCount > 0)
                    ++index

                viewDataBinding.chipGroup.addView(placeChip, index)
                isPlaceSelected = true
            } ?: run {
                placeChip?.let {
                    viewDataBinding.chipGroup.removeView(it)
                    isPlaceSelected = false
                }
            }
        })

        viewModel.checkedTag.observe(viewLifecycleOwner, { tag ->
            tag?.let {
                val tagId = tag.id
                val text = tag.name

                val chip = Chip(requireContext()).apply {
                    this.tag = tagId
                    this.text = text
                    setOnCloseIconClickListener {
                        viewModel.removeTagWithPostList(tagId)
                    }
                }

                if (tagChipList.add(chip))
                    viewDataBinding.chipGroup.addView(chip)
            }
        })

        viewModel.uncheckedTag.observe(viewLifecycleOwner, { tag ->
            tagChipList.singleOrNull { chip -> chip.tag == tag.id }?.let {
                if (tagChipList.remove(it))
                    viewDataBinding.chipGroup.removeView(it)
            }
        })
    }

    override fun onDestroyView() {
        compositeDisposable.clear()
        super.onDestroyView()
    }
}