package com.grand.duke.elliot.restaurantpost.ui.place

import com.grand.duke.elliot.restaurantpost.application.MainApplication
import com.grand.duke.elliot.restaurantpost.repository.data.DisplayPlace
import com.grand.duke.elliot.restaurantpost.ui.util.dialog_fragment.SearchBarListAdapter
import com.grand.duke.elliot.restaurantpost.ui.util.dialog_fragment.SearchBarListItem
import com.grand.duke.elliot.restaurantpost.ui.util.hide
import com.grand.duke.elliot.restaurantpost.ui.util.setTextWithSearchWordColorChange
import com.grand.duke.elliot.restaurantpost.ui.util.show

class DisplayPlaceListAdapter(private val useCheckBox: Boolean = false): SearchBarListAdapter<DisplayPlace>() {

    private var onPlaceCheckedChangeListener: OnPlaceCheckedChangeListener? = null

    fun setOnPlaceCheckedChangeListener(onPlaceCheckedChangeListener: OnPlaceCheckedChangeListener) {
        this.onPlaceCheckedChangeListener = onPlaceCheckedChangeListener
    }

    interface OnPlaceCheckedChangeListener {
        fun onPlaceCheckedChange(id: Long, isChecked: Boolean)
    }

    override fun deepCopy(item: DisplayPlace): DisplayPlace = item.deepCopy()

    override fun bind(viewHolder: ViewHolder, position: Int, searchBarListItem: SearchBarListItem<DisplayPlace>) {
        val displayPlace = searchBarListItem.item

        if (useCheckBox) {
            viewHolder.binding.appCompatCheckBox.show()
            viewHolder.binding.appCompatCheckBox.setOnCheckedChangeListener { _, b ->
                onPlaceCheckedChangeListener?.onPlaceCheckedChange(displayPlace.place.id, b)
            }
        } else
            viewHolder.binding.appCompatCheckBox.hide()

        viewHolder.binding.imageView.hide()
        viewHolder.binding.imageViewMore.show()
        viewHolder.binding.textViewCount.show()
        viewHolder.binding.viewColorBar.hide()

        setTextWithSearchWordColorChange(
                viewHolder.binding.textViewName,
                displayPlace.place.name,
                searchWord,
                MainApplication.themePrimaryColor
        )

        viewHolder.binding.textViewCount.text = displayPlace.postListCount.toString()

        viewHolder.binding.imageViewMore.setOnClickListener {
            showPopupMenu(it, displayPlace, viewHolder.absoluteAdapterPosition)
        }

        viewHolder.binding.root.setOnClickListener {
            onItemClickListener?.onItemClick(searchBarListItem.item, viewHolder.absoluteAdapterPosition)

            if (useCheckBox)
                viewHolder.binding.appCompatCheckBox.toggle()
        }
    }

    override fun filter(searchBarListItem: SearchBarListItem<DisplayPlace>, searchWord: String): SearchBarListItem<DisplayPlace>? {
        if (searchBarListItem.item.place.name.contains(searchWord))
            return searchBarListItem

        return null
    }
}