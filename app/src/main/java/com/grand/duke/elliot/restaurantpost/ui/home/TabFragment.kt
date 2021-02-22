package com.grand.duke.elliot.restaurantpost.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.grand.duke.elliot.restaurantpost.R
import com.grand.duke.elliot.restaurantpost.base.BaseFragment
import com.grand.duke.elliot.restaurantpost.databinding.FragmentTabDrawerBinding
import com.grand.duke.elliot.restaurantpost.repository.data.DisplayFolder
import com.grand.duke.elliot.restaurantpost.repository.data.DisplayPlace
import com.grand.duke.elliot.restaurantpost.repository.data.DisplayTag
import com.grand.duke.elliot.restaurantpost.ui.calendar.CalendarFragment
import com.grand.duke.elliot.restaurantpost.ui.drawer.DrawerItem
import com.grand.duke.elliot.restaurantpost.ui.drawer.DrawerItemAdapter
import com.grand.duke.elliot.restaurantpost.ui.folder.DisplayFolderListAdapter
import com.grand.duke.elliot.restaurantpost.ui.place.DisplayPlaceListAdapter
import com.grand.duke.elliot.restaurantpost.ui.post.list.PostListFragment
import com.grand.duke.elliot.restaurantpost.ui.post.writing.WritingActivity
import com.grand.duke.elliot.restaurantpost.ui.tag.DisplayTagListAdapter
import com.grand.duke.elliot.restaurantpost.ui.util.dialog_fragment.SearchBarListItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.tab_navigation_view.view.*
import timber.log.Timber

class TabFragment: BaseFragment<MainViewModel, FragmentTabDrawerBinding>() {

    private lateinit var uiController: UiController
    private val compositeDisposable by lazy {
        CompositeDisposable()
    }
    private val displayFolderListAdapter = DisplayFolderListAdapter()
    private val displayPlaceAdapter = DisplayPlaceListAdapter(true)
    private val displayTagListAdapter = DisplayTagListAdapter(true)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        uiController = UiController(viewDataBinding)
        uiController.init()
        initFlowable()
        return view
    }

    private inner class UiController(private val binding: FragmentTabDrawerBinding) {

        private val context = binding.root.context
        private val fragmentTabLayout = binding.fragmentTabLayout

        private val tabTexts = arrayOf(
                context.getString(R.string.post),
                context.getString(R.string.calendar)
        )

        fun init() {
            fragmentTabLayout.floatingActionButton.setOnClickListener {
                startActivity(Intent(requireActivity(), WritingActivity::class.java))
            }

            initDrawer()
            initTabLayout()
        }

        private fun initTabLayout() {
            fragmentTabLayout.viewPager.adapter = FragmentStateAdapter(requireActivity())
            fragmentTabLayout.viewPager.isUserInputEnabled = true

            TabLayoutMediator(fragmentTabLayout.tabLayout, fragmentTabLayout.viewPager) { tab, position ->
                tab.tag = position
                // tab.icon = ContextCompat.getDrawable(requireContext(), tabIcons[position])
                tab.text = tabTexts[position]
            }.attach()
        }

        private fun initDrawer() {
            (requireActivity() as MainActivity).setSupportActionBar(binding.fragmentTabLayout.toolbar)

            val toggle = ActionBarDrawerToggle(
                    requireActivity(),
                    binding.drawerLayout, binding.fragmentTabLayout.toolbar,
                    R.string.ok,
                    R.string.cancel
            )

            binding.drawerLayout.addDrawerListener(toggle)
            toggle.isDrawerIndicatorEnabled = true
            toggle.syncState()

            displayTagListAdapter.apply {
                setOnTagCheckedChangeListener(object: DisplayTagListAdapter.OnTagCheckedChangeListener {
                    override fun onTagCheckedChange(id: Long, isChecked: Boolean) {

                    }
                })
            }

            @Suppress("UNCHECKED_CAST")
            binding.navigationViewLayout.recycler_view.adapter = DrawerItemAdapter(
                    arrayListOf(
                            DrawerItem.ListItem(
                                    id = 0L,
                                    adapter = displayFolderListAdapter as ListAdapter<SearchBarListItem<DisplayFolder>, RecyclerView.ViewHolder>,
                                    title = getString(R.string.folder),
                                    iconColor = getColor(R.color.color_icon),
                                    iconResourceId = R.drawable.ic_round_folder_24,
                                    onHeaderClick = null, // todo imp.
                                    onMoreIconClick = null // todo imp.
                            ),
                            DrawerItem.ListItem(
                                    id = 1L,
                                    adapter = displayTagListAdapter as ListAdapter<SearchBarListItem<DisplayFolder>, RecyclerView.ViewHolder>,
                                    title = getString(R.string.tag),
                                    iconColor = getColor(R.color.color_icon),
                                    iconResourceId = R.drawable.ic_round_tag_24,
                                    onHeaderClick = null, // todo imp.
                                    onMoreIconClick = null // todo imp.
                            ),
                            DrawerItem.ListItem(
                                    id = 2L,
                                    adapter = displayPlaceAdapter as ListAdapter<SearchBarListItem<DisplayPlace>, RecyclerView.ViewHolder>,
                                    title = getString(R.string.place),
                                    iconColor = getColor(R.color.color_icon),
                                    iconResourceId = R.drawable.ic_round_location_on_24,
                                    onHeaderClick = null, // todo imp.
                                    onMoreIconClick = null // todo imp.
                            )
                    )
            )
        }
    }

    @Suppress("SpellCheckingInspection")
    private fun initFlowable() {
        compositeDisposable.add(viewModel.folderWithPostListFlowable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    displayFolderListAdapter.submitItemList(it.map { folderWithPostList ->
                        DisplayFolder(folderWithPostList.folder, folderWithPostList.postList.count())
                    })
                }, {
                    Timber.e(it)
                }))

        compositeDisposable.add(viewModel.tagWithPostListFlowable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    displayTagListAdapter.submitItemList(it.map { tagWithPostList ->
                        DisplayTag(tagWithPostList.tag, tagWithPostList.postList.count())
                    })
                }, {
                    Timber.e(it)
                }))

        compositeDisposable.add(viewModel.placeWithPostListFlowable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    displayPlaceAdapter.submitItemList(it.map { placeWithPostList ->
                        val place = placeWithPostList.place
                        val postList = placeWithPostList.postList
                        DisplayPlace(place, postList.count())
                    })
                }, {
                    Timber.e(it)
                }))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    @Suppress("SameParameterValue")
    private fun getColor(@ColorRes id: Int) = ContextCompat.getColor(requireContext(), id)

    class FragmentStateAdapter(fragmentActivity: FragmentActivity):
            androidx.viewpager2.adapter.FragmentStateAdapter(fragmentActivity) {
        private val pageCount = 2

        override fun getItemCount(): Int = pageCount

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> PostListFragment()
                1 -> CalendarFragment()
                else -> throw IllegalArgumentException("Invalid position.")
            }
        }
    }

    override val layoutRes: Int
        get() = R.layout.fragment_tab_drawer

    override fun viewModel(): Class<MainViewModel> = MainViewModel::class.java
}