package com.grand.duke.elliot.restaurantpost.ui.drawer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.grand.duke.elliot.restaurantpost.databinding.DividerBinding
import com.grand.duke.elliot.restaurantpost.databinding.ItemDrawerContentBinding
import com.grand.duke.elliot.restaurantpost.databinding.ItemDrawerListBinding
import com.grand.duke.elliot.restaurantpost.databinding.ItemDrawerSubtitleBinding
import java.lang.IllegalArgumentException

class DrawerItemAdapter(private val drawerItemList: ArrayList<DrawerItem>):
        RecyclerView.Adapter<DrawerItemAdapter.ViewHolder>(){

    class ViewHolder(private val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(drawerItem: DrawerItem) {
            when(drawerItem) {
                is DrawerItem.ContentItem -> {
                    binding as ItemDrawerContentBinding
                    drawerItem.iconResourceId?.also {
                        binding.imageViewIcon.setImageResource(it)
                        drawerItem.iconColor?.also { binding.imageViewIcon.setColorFilter(it) }
                    }
                    binding.root.setOnClickListener {
                        drawerItem.onClickListener?.invoke()
                    }
                }
                is DrawerItem.DividerItem -> { /** Divider. */ }
                is DrawerItem.ListItem<*> -> {
                    binding as ItemDrawerListBinding
                    binding.textViewTitle.text = drawerItem.title
                    binding.recyclerView.apply {
                        layoutManager = LinearLayoutManager(binding.root.context)
                        adapter = drawerItem.adapter
                    }
                }
                is DrawerItem.SubtitleItem -> {
                    binding as ItemDrawerSubtitleBinding
                    binding.textViewSubtitle.text = drawerItem.title
                }
            }
        }
    }

    private object ViewType {
        const val Content = 0
        const val Divider = 1
        const val List = 2
        const val Subtitle = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when(drawerItemList[position]) {
            is DrawerItem.ContentItem -> ViewType.Content
            is DrawerItem.DividerItem -> ViewType.Divider
            is DrawerItem.ListItem<*> -> ViewType.List
            is DrawerItem.SubtitleItem -> ViewType.Subtitle
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding =  when(viewType) {
            ViewType.Content -> ItemDrawerContentBinding.inflate(layoutInflater, parent, false)
            ViewType.Divider -> DividerBinding.inflate(layoutInflater, parent, false)
            ViewType.List -> ItemDrawerListBinding.inflate(layoutInflater, parent, false)
            ViewType.Subtitle -> ItemDrawerSubtitleBinding.inflate(layoutInflater, parent, false)
            else -> throw IllegalArgumentException("Invalid viewType.")
        }

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(drawerItemList[position])
    }

    override fun getItemCount(): Int = drawerItemList.count()
}

sealed class DrawerItem() {
    class ContentItem (
            override val id: Long,
            var title: String? = null,
            var iconColor: Int? = null,
            var iconResourceId: Int? = null,
            var onClickListener: (() -> Unit)? = null
    ): DrawerItem()

    class DividerItem(
            override val id: Long
    ): DrawerItem()

    class ListItem<T> (
            override val id: Long,
            var adapter: ListAdapter<T, RecyclerView.ViewHolder>,
            var title: String? = null,
            @DrawableRes var iconResourceId: Int? = null,
            @ColorInt var iconColor: Int? = null,
            @ColorInt var arrowDropDownColor: Int? = null,
            var onHeaderClick: ((ItemDrawerListBinding) -> Unit)? = null,
            @ColorInt var moreIconColor: Int? = null,
            var onMoreIconClick: (() -> Unit)? = null,
    ): DrawerItem()

    class SubtitleItem (
            override val id: Long,
            val title: String
    ): DrawerItem()

    abstract val id: Long
}