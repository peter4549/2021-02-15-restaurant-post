package com.grand.duke.elliot.restaurantpost.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.grand.duke.elliot.restaurantpost.R
import com.grand.duke.elliot.restaurantpost.databinding.FragmentTabBinding
import com.grand.duke.elliot.restaurantpost.ui.calendar.CalendarFragment
import com.grand.duke.elliot.restaurantpost.ui.post.list.PostListFragment
import com.grand.duke.elliot.restaurantpost.ui.post.writing.WritingActivity

class TabFragment: Fragment() {

    private lateinit var binding: FragmentTabBinding
    private lateinit var uiController: UiController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTabBinding.inflate(inflater, container, false)
        uiController = UiController(binding)
        uiController.init()
        return binding.root
    }

    private inner class UiController(binding: FragmentTabBinding) {

        private val context = binding.root.context

        private val tabTexts = arrayOf(
                context.getString(R.string.post),
                context.getString(R.string.calendar)
        )

        fun init() {
            binding.floatingActionButton.setOnClickListener {
                startActivity(Intent(requireActivity(), WritingActivity::class.java))
            }

            initTabLayout()
        }

        private fun initTabLayout() {
            binding.viewPager.adapter = FragmentStateAdapter(requireActivity())
            binding.viewPager.isUserInputEnabled = true

            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                tab.tag = position
                // tab.icon = ContextCompat.getDrawable(requireContext(), tabIcons[position])
                tab.text = tabTexts[position]
            }.attach()
        }
    }

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
}