package com.grand.duke.elliot.restaurantpost.ui.post.list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.grand.duke.elliot.restaurantpost.R
import com.grand.duke.elliot.restaurantpost.databinding.ItemDateBinding
import com.grand.duke.elliot.restaurantpost.databinding.ItemFramePostBinding
import com.grand.duke.elliot.restaurantpost.persistence.data.Post
import com.grand.duke.elliot.restaurantpost.ui.util.blank
import com.grand.duke.elliot.restaurantpost.ui.util.hide
import com.grand.duke.elliot.restaurantpost.ui.util.show
import com.grand.duke.elliot.restaurantpost.ui.util.toSimpleDateFormat
import java.lang.IllegalArgumentException

class PostAdapter: ListAdapter<AdapterItem, PostAdapter.ViewHolder>(PostDiffCallback()) {

    private var onItemClickListener: OnItemClickListener? = null
    private var recyclerView: RecyclerView? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(holder: ViewHolder, post: Post)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    inner class ViewHolder(val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(adapterItem: AdapterItem) {
            when(adapterItem) {
                is AdapterItem.DateItem -> {
                    binding as ItemDateBinding
                    binding.textView.text = adapterItem.dateString
                }
                is AdapterItem.PostItem -> {
                    binding as ItemFramePostBinding
                    val post = adapterItem.post

                    post.photoUriStringArray.let {
                        if (it.isEmpty())
                            binding.constraintLayoutViewPager.hide()
                        else {
                            binding.constraintLayoutViewPager.show()
                            binding.viewPager.adapter = PhotoPagerAdapter(it)
                            binding.wormDotsIndicator.setViewPager(binding.viewPager)
                        }
                    }

                    binding.textViewTitle.text = post.description

                    binding.root.setOnClickListener {
                        onItemClickListener?.onItemClick(this, post)
                    }

                    binding.viewPager.setSingleTapUpListener {
                        onItemClickListener?.onItemClick(this, post)
                    }
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
        const val Date = 1
    }

    fun submitPostList(list: List<Post>, context: Context) {
        val adapterItemList = ArrayList<AdapterItem>()
        var dateString = blank

        for ((index, item) in list.withIndex()) {
            val new = item.modifiedTime.toSimpleDateFormat(context.getString(R.string.pattern_year_month_date))

            if (new != dateString) {
                dateString = new
                adapterItemList.add(AdapterItem.DateItem(-index.toLong(), dateString))
            }

            adapterItemList.add(AdapterItem.PostItem(item))
        }

        recyclerView?.scheduleLayoutAnimation()

        super.submitList(adapterItemList)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is AdapterItem.PostItem -> ViewType.Data
            is AdapterItem.DateItem -> ViewType.Date
            else -> throw IllegalArgumentException("Invalid item.")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = when(viewType) {
            ViewType.Data -> ItemFramePostBinding.inflate(layoutInflater, parent, false)
            ViewType.Date -> ItemDateBinding.inflate(layoutInflater, parent, false)
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
    data class PostItem(val post: Post): AdapterItem() {
        override val id = post.id
    }

    data class DateItem(
            override val id: Long,
            val dateString: String
    ): AdapterItem()

    abstract val id: Long
}