package com.grand.duke.elliot.restaurantpost.ui.post.photo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.grand.duke.elliot.restaurantpost.databinding.FragmentPhotoListBinding

class PhotoListFragment: Fragment() {

    private lateinit var binding: FragmentPhotoListBinding

    object Key {
        const val PhotoUriStringArrayList = "com.grand.duke.elliot.restaurantpost.ui.post.photo" +
                ".photo_list_fragment.key.photo_uri_string_array_list"
    }

    private val photoAdapter: PhotoAdapter by lazy { PhotoAdapter() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPhotoListBinding.inflate(inflater, container, false)

        arguments?.let {
            val photoUriStringArrayList = it.getStringArrayList(Key.PhotoUriStringArrayList)

            if (photoUriStringArrayList == null)
                requireActivity().onBackPressed()

            binding.viewPager.adapter = photoAdapter.apply {
                submitList(photoUriStringArrayList)
            }
        } ?: run {
            requireActivity().onBackPressed()
        }

        return binding.root
    }
}