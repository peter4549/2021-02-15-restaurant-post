package com.grand.duke.elliot.restaurantpost.ui.tag

import android.view.View
import com.grand.duke.elliot.restaurantpost.application.MainApplication
import com.grand.duke.elliot.restaurantpost.repository.data.DisplayTag
import com.grand.duke.elliot.restaurantpost.ui.util.dialog_fragment.AdapterItem
import com.grand.duke.elliot.restaurantpost.ui.util.dialog_fragment.SearchBarListAdapter
import com.grand.duke.elliot.restaurantpost.ui.util.setTextWithSearchWordColorChange

class DisplayTagListAdapter: SearchBarListAdapter<DisplayTag>() {

    override fun deepCopy(item: DisplayTag): DisplayTag = item.deepCopy()

    override fun bind(viewHolder: ViewHolder, adapterItem: AdapterItem<DisplayTag>) {
        val displayTag = adapterItem.item
        viewHolder.binding.imageView.visibility = View.GONE
        viewHolder.binding.imageViewMore.visibility = View.VISIBLE
        viewHolder.binding.textViewCount.visibility = View.VISIBLE
        viewHolder.binding.viewColorBar.visibility = View.GONE

        setTextWithSearchWordColorChange(
                viewHolder.binding.textViewName,
                displayTag.tag.name,
                searchWord,
                MainApplication.themePrimaryColor
        )

        viewHolder.binding.textViewCount.text = displayTag.postListCount.toString()

        viewHolder.binding.imageViewMore.setOnClickListener {
            onItemClickListener?.onMoreClick(adapterItem.item, viewHolder.adapterPosition)
        }

        viewHolder.binding.root.setOnClickListener {
            onItemClickListener?.onItemClick(adapterItem.item, viewHolder.adapterPosition)
        }
    }

    override fun filter(adapterItem: AdapterItem<DisplayTag>, searchWord: String): AdapterItem<DisplayTag>? {
        if (adapterItem.item.tag.name.contains(searchWord))
            return adapterItem

        return null
    }
}