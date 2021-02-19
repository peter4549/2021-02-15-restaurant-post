package com.grand.duke.elliot.restaurantpost.ui.post.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.grand.duke.elliot.restaurantpost.databinding.ItemFramePostBinding
import com.grand.duke.elliot.restaurantpost.persistence.data.Post
import com.grand.duke.elliot.restaurantpost.ui.util.hide
import com.grand.duke.elliot.restaurantpost.ui.util.show
import java.lang.IllegalArgumentException

class PostAdapter: ListAdapter<AdapterItem, PostAdapter.ViewHolder>(PostDiffCallback()) {

    class ViewHolder(private val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(adapterItem: AdapterItem) {
            when(adapterItem) {
                is AdapterItem.Header -> {}
                is AdapterItem.DataItem -> {
                    binding as ItemFramePostBinding
                    val post = adapterItem.post

                    post.photoUriStringArray.let {
                        if (it.isEmpty())
                            binding.relativeLayoutViewPager.hide()
                        else {
                            binding.relativeLayoutViewPager.show()
                            binding.viewPager.adapter = PhotoPagerAdapter(it)
                        }
                    }

                    binding.textViewTitle.text = post.description
                }
            }
        }
    }

    object ViewMode {
        const val Compact = 0
        const val Frame = 1
    }

    object ViewType {
        const val Data = 0
        const val Header = 1
    }

    fun setViewMode() {

    }

    fun submitDataList(list: List<Post>) {
        // TODO add header, date logic needed..

        val adapterItemList = list.map { AdapterItem.DataItem(it) }

        super.submitList(adapterItemList)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is AdapterItem.DataItem -> ViewType.Data
            is AdapterItem.Header -> ViewType.Header
            else -> throw IllegalArgumentException("Invalid item.")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = when(viewType) {
            ViewType.Data -> ItemFramePostBinding.inflate(layoutInflater, parent, false)
            else -> throw IllegalArgumentException("Invalid viewType.")
        }

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class PostDiffCallback: DiffUtil.ItemCallback<AdapterItem>() {
    override fun areItemsTheSame(oldItem: AdapterItem, newItem: AdapterItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: AdapterItem, newItem: AdapterItem): Boolean {
        return oldItem == newItem
    }
}

sealed class AdapterItem {
    data class DataItem(val post: Post): AdapterItem() {
        override val id = post.id
    }

    object Header: AdapterItem() {
        override val id = Long.MIN_VALUE
    }

    abstract val id: Long
}