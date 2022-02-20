package com.smart.browser.little.ui.games

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.smart.browser.little.R
import com.smart.browser.little.ad.NativeAdViewHolder
import com.smart.browser.little.ui.home.PlaceholderViewHolder
import com.art.maker.data.adapter.SitesAdapter
import com.art.maker.data.model.Site
import com.art.maker.data.model.SiteSection
import com.art.maker.util.Log

/**
 * 分类适配器.
 *
 * @author yushaojian
 * @date 2021-05-05 15:10
 */
abstract class AdvertSectionAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected abstract val sectionLayoutId: Int
        @LayoutRes get

    protected abstract val itemLayoutId: Int
        @LayoutRes get

    private var sections = ArrayList<GameItem>()

    fun getItem(position: Int) = sections[position]

    fun submitList(sections: ArrayList<GameItem>) {
        //this.sections.clear()
        //this.sections.addAll(sections)
        this.sections = sections
        notifyDataSetChanged()
    }

    /*override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SectionViewHolder(inflater.inflate(sectionLayoutId, parent, false))
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == R.layout.ad_native_home) return NativeAdViewHolder.from(parent)
        if (viewType == R.layout.item_game_section)
            return SectionViewHolder(inflater.inflate(sectionLayoutId, parent, false))
        return PlaceholderViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //holder.bind(sections[position])
        val item = sections[position]
        Log.d("onBindViewHolder", "pos [$position] item [$item]")
        if (item is AdItem && holder is NativeAdViewHolder) {
            holder.bind(item.nativeAd)
        } else if (item is SitesItem && holder is SectionViewHolder) {
            holder.bind(item.siteSection)
        } else if (item is PlaceholderItem) {
            //placeholder
        }
    }

    override fun getItemCount(): Int = sections.size

    override fun getItemViewType(position: Int): Int {
        return when (sections[position]) {
            is SitesItem -> R.layout.item_game_section
            is AdItem -> R.layout.ad_native_home
            is PlaceholderItem -> R.layout.item_placeholder
        }
    }

    protected open fun onAppClick(section: SiteSection, site: Site) {}

    protected open fun onCategoryClick(name: String, key: String) {}

    protected open fun itemDecoration(context: Context): RecyclerView.ItemDecoration? = null

    inner class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val titleTV: TextView = itemView.findViewById(R.id.titleTV)
        private val seeAllTV: TextView = itemView.findViewById(R.id.seeAllTV)
        private val appsRV: RecyclerView = itemView.findViewById(R.id.appsRV)

        init {
            val itemDecoration = itemDecoration(itemView.context)
            itemDecoration?.let { appsRV.addItemDecoration(it) }
        }

        private val appAdapter = object : SitesAdapter() {
            override fun onAppClick(site: Site) {
                super.onAppClick(site)
                onAppClick((sections[bindingAdapterPosition] as SitesItem).siteSection, site)
            }
        }

        init {
            appsRV.adapter = appAdapter
            seeAllTV.setOnClickListener {
                val appSection = (sections[bindingAdapterPosition] as SitesItem).siteSection
                val key = appSection.key
                if (!key.isNullOrBlank()) {
                    onCategoryClick(appSection.name, key)
                }
            }
        }

        fun bind(section: SiteSection) {
            titleTV.text = section.name
            seeAllTV.visibility = if (section.key.isNullOrBlank()) View.GONE else View.VISIBLE
            appAdapter.setApps(section.sites)
        }

    }

    fun replace(position: Int, gameItem: GameItem) {
        if (position < 0 || position >= itemCount) return
        sections[position] = gameItem
        notifyItemChanged(position)
    }

}