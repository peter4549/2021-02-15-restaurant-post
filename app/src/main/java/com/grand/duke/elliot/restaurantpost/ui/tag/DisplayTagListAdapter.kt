package com.grand.duke.elliot.restaurantpost.ui.tag

import com.grand.duke.elliot.restaurantpost.application.MainApplication
import com.grand.duke.elliot.restaurantpost.repository.data.DisplayTag
import com.grand.duke.elliot.restaurantpost.ui.util.dialog_fragment.SearchBarListAdapter
import com.grand.duke.elliot.restaurantpost.ui.util.dialog_fragment.SearchBarListItem
import com.grand.duke.elliot.restaurantpost.ui.util.hide
import com.grand.duke.elliot.restaurantpost.ui.util.setTextWithSearchWordColorChange
import com.grand.duke.elliot.restaurantpost.ui.util.show

class DisplayTagListAdapter(private val useCheckBox: Boolean = false): SearchBarListAdapter<DisplayTag>() {

    private val positionOf = hashMapOf<Long, Int>()
    private var uncheckedTagPosition = -1

    private var onTagCheckedChangeListener: OnTagCheckedChangeListener? = null

    fun setOnTagCheckedChangeListener(onTagCheckedChangeListener: OnTagCheckedChangeListener) {
        this.onTagCheckedChangeListener = onTagCheckedChangeListener
    }

    interface OnTagCheckedChangeListener {
        fun onTagCheckedChange(id: Long, isChecked: Boolean)
    }

    override fun deepCopy(item: DisplayTag): DisplayTag = item.deepCopy()

    override fun bind(viewHolder: ViewHolder, position: Int, searchBarListItem: SearchBarListItem<DisplayTag>) {
        val displayTag = searchBarListItem.item

        if (useCheckBox) {
            viewHolder.binding.appCompatCheckBox.show()
            viewHolder.binding.appCompatCheckBox.setOnCheckedChangeListener { _, b ->
                onTagCheckedChangeListener?.onTagCheckedChange(displayTag.tag.id, b)
            }
        } else
            viewHolder.binding.appCompatCheckBox.hide()

        viewHolder.binding.imageView.hide()
        viewHolder.binding.imageViewMore.show()
        viewHolder.binding.textViewCount.show()
        viewHolder.binding.viewColorBar.hide()

        setTextWithSearchWordColorChange(
            viewHolder.binding.textViewName,
            displayTag.tag.name,
            searchWord,
            MainApplication.themePrimaryColor
        )

        viewHolder.binding.textViewCount.text = displayTag.postListCount.toString()

        viewHolder.binding.imageViewMore.setOnClickListener {
            showPopupMenu(it, displayTag, viewHolder.absoluteAdapterPosition)
        }

        viewHolder.binding.root.setOnClickListener {
            onItemClickListener?.onItemClick(
                searchBarListItem.item,
                viewHolder.absoluteAdapterPosition
            )

            if (useCheckBox) {
                viewHolder.binding.appCompatCheckBox.toggle()

                if (viewHolder.binding.appCompatCheckBox.isChecked)
                    positionOf[displayTag.tag.id] = position
                else
                    positionOf[displayTag.tag.id] = -1
            }
        }

        if (position == uncheckedTagPosition) {
            viewHolder.binding.appCompatCheckBox.isChecked = false
            uncheckedTagPosition = -1
        }
    }

    fun uncheck(id: Long) {
        positionOf[id]?.let {
            uncheckedTagPosition = it
            notifyItemChanged(it)
        }
    }

    override fun filter(searchBarListItem: SearchBarListItem<DisplayTag>, searchWord: String): SearchBarListItem<DisplayTag>? {
        if (searchBarListItem.item.tag.name.contains(searchWord))
            return searchBarListItem

        return null
    }
}