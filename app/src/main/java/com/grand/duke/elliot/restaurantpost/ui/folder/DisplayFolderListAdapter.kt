package com.grand.duke.elliot.restaurantpost.ui.folder

import android.view.View
import com.grand.duke.elliot.restaurantpost.repository.data.DisplayFolder
import com.grand.duke.elliot.restaurantpost.ui.util.dialog_fragment.AdapterItem
import com.grand.duke.elliot.restaurantpost.ui.util.dialog_fragment.SearchBarListAdapter
import com.grand.duke.elliot.restaurantpost.ui.util.setTextWithSearchWordColorChange

class DisplayFolderListAdapter : SearchBarListAdapter<DisplayFolder>() {

    override fun deepCopy(item: DisplayFolder): DisplayFolder = item.deepCopy()

    override fun bind(viewHolder: ViewHolder, adapterItem: AdapterItem<DisplayFolder>) {
        val displayFolder = adapterItem.item
        viewHolder.binding.imageView.visibility = View.GONE
        viewHolder.binding.imageViewMore.visibility = View.VISIBLE
        viewHolder.binding.textViewCount.visibility = View.VISIBLE
        viewHolder.binding.viewColorBar.visibility = View.VISIBLE

        setTextWithSearchWordColorChange(
            viewHolder.binding.textViewName,
            displayFolder.folder.name,
            searchWord,
            displayFolder.folder.color
        )
        viewHolder.binding.textViewCount.text = displayFolder.postListCount.toString()
        viewHolder.binding.viewColorBar.setBackgroundColor(displayFolder.folder.color)

        viewHolder.binding.imageViewMore.setOnClickListener {
            onItemClickListener?.onMoreClick(adapterItem.item, viewHolder.adapterPosition)
        }

        viewHolder.binding.root.setOnClickListener {
            onItemClickListener?.onItemClick(adapterItem.item, viewHolder.adapterPosition)
        }
    }

    override fun filter(adapterItem: AdapterItem<DisplayFolder>, searchWord: String): AdapterItem<DisplayFolder>? {
        if (adapterItem.item.folder.name.contains(searchWord))
            return adapterItem

        return null
    }
}