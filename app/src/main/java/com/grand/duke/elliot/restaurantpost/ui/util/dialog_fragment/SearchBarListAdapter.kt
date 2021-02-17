package com.grand.duke.elliot.restaurantpost.ui.util.dialog_fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.grand.duke.elliot.restaurantpost.R
import com.grand.duke.elliot.restaurantpost.databinding.ItemListBinding
import com.grand.duke.elliot.restaurantpost.ui.util.blank

abstract class SearchBarListAdapter<T>: ListAdapter<AdapterItem<T>, SearchBarListAdapter.ViewHolder>(DiffCallback<T>()) {

    protected abstract fun deepCopy(item: T): T
    private val adapterItemList = arrayListOf<AdapterItem<T>>()

    protected lateinit var recyclerView: RecyclerView

    protected var onItemClickListener: OnItemClickListener<T>? = null

    @JvmName("_setOnItemClickListener")
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener<T>) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener<T> {
        fun onDeleteClick(item: T)
        fun onEditClick(item: T)
        fun onItemClick(item: T, adapterPosition: Int)
    }

    fun submitItemList(itemList: List<T>) {
        adapterItemList.clear()

        for ((index, item) in itemList.withIndex())
            adapterItemList.add(AdapterItem(index.toLong(), item))

        submitList(ArrayList(adapterItemList))
    }

    protected var menuRes = R.menu.menu_search_bar_list_adapter
    protected var searchWord = blank

    class ViewHolder(val binding: ItemListBinding): RecyclerView.ViewHolder(binding.root)

    protected abstract fun bind(viewHolder: ViewHolder, adapterItem: AdapterItem<T>)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        bind(holder, getItem(position))
    }

    protected abstract fun filter(adapterItem: AdapterItem<T>, searchWord: String): AdapterItem<T>?

    fun getFilter(): Filter {
        var adapterItemListFiltered: ArrayList<AdapterItem<T>>

        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                searchWord = charSequence.toString()
                val adapterItemListBeingFiltered = arrayListOf<AdapterItem<T>>()

                adapterItemListFiltered =
                    if (searchWord.isBlank())
                        adapterItemList
                    else {
                        for (item in adapterItemList)
                            filter(item, searchWord)?.also {
                                val adapterItem = AdapterItem(
                                    it.id,
                                    deepCopy(it.item)
                                )
                                adapterItemListBeingFiltered.add(adapterItem)
                            }

                        adapterItemListBeingFiltered
                    }

                return FilterResults().apply {
                    values = adapterItemListFiltered
                }
            }

            override fun publishResults(charSequence: CharSequence?, results: FilterResults?) {
                @Suppress("UNCHECKED_CAST")
                results?.values?.also {
                    submitList(results.values as List<AdapterItem<T>>)

                    if (searchWord.isBlank())
                        notifyDataSetChanged()
                }
            }
        }
    }

    protected fun showPopupMenu(view: View, item: T, position: Int) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(menuRes)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_edit -> {
                    onItemClickListener?.onEditClick(item)
                    true
                }
                R.id.item_delete -> {
                    removeAt(position)
                    onItemClickListener?.onDeleteClick(item)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun removeAt(position: Int) {
        adapterItemList.removeAt(position)
        notifyItemRemoved(position)
    }
}

class DiffCallback<T>: DiffUtil.ItemCallback<AdapterItem<T>>() {
    override fun areItemsTheSame(oldItem: AdapterItem<T>, newItem: AdapterItem<T>): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: AdapterItem<T>, newItem: AdapterItem<T>): Boolean {
        return oldItem == newItem
    }
}

data class AdapterItem<T>(
    val id: Long,
    val item: T
)