package com.art.maker.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.smart.browser.little.data.R
import com.art.maker.data.model.Site
import com.art.maker.data.model.SiteSection

/**
 * 分类适配器.
 *
 * @author yushaojian
 * @date 2021-05-05 15:10
 */
abstract class SectionAdapter :
    RecyclerView.Adapter<SectionAdapter.SectionViewHolder>() {

    protected abstract val sectionLayoutId: Int
        @LayoutRes get

    protected abstract val itemLayoutId: Int
        @LayoutRes get

    private val sections = ArrayList<SiteSection>()

    fun submitList(sections: List<SiteSection>) {
        this.sections.clear()
        this.sections.addAll(sections)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SectionViewHolder(inflater.inflate(sectionLayoutId, parent, false))
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        holder.bind(sections[position])
    }

    override fun getItemCount(): Int = sections.size

    protected open fun onAppClick(section: SiteSection, site: Site) {}

    protected open fun onCategoryClick(name: String, key: String) {}

    protected open fun itemDecoration(context: Context) : RecyclerView.ItemDecoration? = null

    inner class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val titleTV: TextView = itemView.findViewById(R.id.titleTV)
        private val seeAllTV: TextView = itemView.findViewById(R.id.seeAllTV)
        private val appsRV: RecyclerView = itemView.findViewById(R.id.appsRV)

        init {
            val itemDecoration = itemDecoration(itemView.context)
            itemDecoration?.let { appsRV.addItemDecoration(it) }
        }

        private val appAdapter = object : SitesAdapter(itemLayoutId) {
            override fun onAppClick(site: Site) {
                super.onAppClick(site)
                onAppClick(sections[bindingAdapterPosition], site)
            }
        }

        init {
            appsRV.adapter = appAdapter
            seeAllTV.setOnClickListener {
                val appSection = sections[bindingAdapterPosition]
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

}