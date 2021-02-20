package com.grand.duke.elliot.restaurantpost.ui.folder

import android.view.View
import com.grand.duke.elliot.restaurantpost.repository.data.DisplayFolder
import com.grand.duke.elliot.restaurantpost.ui.util.dialog_fragment.SearchBarListItem
import com.grand.duke.elliot.restaurantpost.ui.util.dialog_fragment.SearchBarListAdapter
import com.grand.duke.elliot.restaurantpost.ui.util.setTextWithSearchWordColorChange

class DisplayFolderListAdapter : SearchBarListAdapter<DisplayFolder>() {

    override fun deepCopy(item: DisplayFolder): DisplayFolder = item.deepCopy()

    override fun bind(viewHolder: ViewHolder, searchBarListItem: SearchBarListItem<DisplayFolder>) {
        val displayFolder = searchBarListItem.item
        viewHolder.binding.appCompatCheckBox.visibility = View.GONE
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
            showPopupMenu(it, displayFolder, viewHolder.absoluteAdapterPosition)
        }

        viewHolder.binding.root.setOnClickListener {
            onItemClickListener?.onItemClick(searchBarListItem.item, viewHolder.absoluteAdapterPosition)
        }
    }

    override fun filter(searchBarListItem: SearchBarListItem<DisplayFolder>, searchWord: String): SearchBarListItem<DisplayFolder>? {
        if (searchBarListItem.item.folder.name.contains(searchWord))
            return searchBarListItem

        return null
    }
}