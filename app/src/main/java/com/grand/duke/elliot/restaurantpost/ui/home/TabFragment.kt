package com.grand.duke.elliot.restaurantpost.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.grand.duke.elliot.restaurantpost.databinding.FragmentTabBinding
import com.grand.duke.elliot.restaurantpost.ui.post.WritingActivity

class TabFragment: Fragment() {

    private lateinit var binding: FragmentTabBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTabBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(requireActivity(), WritingActivity::class.java))
        }
    }
}