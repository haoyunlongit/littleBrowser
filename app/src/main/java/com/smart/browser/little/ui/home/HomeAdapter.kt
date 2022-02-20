package com.smart.browser.little.ui.home

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smart.browser.little.R
import com.smart.browser.little.ad.NativeAdViewHolder
import com.art.maker.data.model.Site
import com.art.maker.util.Log

/**
 * 主页Adapter.
 *
 * @author yushaojian
 * @date 2021-06-17 21:40
 */
class HomeAdapter(
    private val unlock: (keyOfCategory: String) -> Unit,
    private val openCategory: (name: String, key: String) -> Unit,
    private val openApp: (site: Site) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var homeItems = ArrayList<HomeItem>()

    fun getItem(position: Int) = homeItems[position]

    fun submitList(homeItems: ArrayList<HomeItem>) {
        //this.homeItems.clear()
        //this.homeItems.addAll(homeItems)
        this.homeItems = homeItems
        notifyDataSetChanged()
    }

    fun replace(position: Int, homeItem: HomeItem) {
        if (position < 0 || position >= itemCount) return
        homeItems[position] = homeItem
        notifyItemChanged(position)
    }

    override fun getItemViewType(position: Int): Int {
        return when (homeItems[position]) {
            is SitesItem -> R.layout.home_item_site_section
            is AdItem -> R.layout.ad_native_home
            is PlaceholderItem -> R.layout.item_placeholder
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == R.layout.ad_native_home) return NativeAdViewHolder.from(parent)
        if (viewType == R.layout.home_item_site_section) return SitesViewHolder.from(
            parent,
            unlock,
            openCategory,
            openApp
        )
        return PlaceholderViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = homeItems[position]
        Log.d("onBindViewHolder","pos [$position] item [$item]")
        if (item is AdItem && holder is NativeAdViewHolder) {
            holder.bind(item.nativeAd)
        } else if (item is SitesItem && holder is SitesViewHolder) {
            holder.bind(item.siteSection, item.unlocked)
        } else if (item is PlaceholderItem) {
           //placeholder
        }
    }

    override fun getItemCount(): Int = homeItems.size

}