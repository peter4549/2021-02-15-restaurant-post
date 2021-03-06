package com.grand.duke.elliot.restaurantpost.ui.post.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.grand.duke.elliot.restaurantpost.R
import kotlinx.android.synthetic.main.item_view_pager.view.image_view

class PhotoPagerAdapter(private val photoUriStringArray: Array<String>): PagerAdapter() {

    override fun getCount(): Int = photoUriStringArray.count()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val photoUriString = photoUriStringArray[position]
        val context = container.context
        val layoutInflater = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        val view = layoutInflater.inflate(R.layout.image_view, container, false)

        Glide.with(context)
                .load(photoUriString)
                .centerCrop()
                .error(R.drawable.ic_round_error_24)
                .into(view.image_view)

        container.addView(view)

        return view
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }
}