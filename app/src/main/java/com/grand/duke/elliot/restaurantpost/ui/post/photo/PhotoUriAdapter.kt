package com.grand.duke.elliot.restaurantpost.ui.post.photo

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.grand.duke.elliot.restaurantpost.R
import kotlinx.android.synthetic.main.item_view_pager.view.*

class PhotoUriAdapter: ListAdapter<Uri, PhotoUriAdapter.ViewHolder>(PhotoUriDiffCallback()) {

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onClick(uri: Uri)
    }

    inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun bind(uri: Uri) {
            Glide.with(view.context)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.ic_round_error_24)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view.image_view)

            view.setOnClickListener {
                onItemClickListener?.onClick(uri)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_pager, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class PhotoUriDiffCallback<T>: DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return true
    }
}