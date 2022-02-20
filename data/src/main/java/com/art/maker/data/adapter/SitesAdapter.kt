package com.art.maker.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.smart.browser.little.data.R
import com.art.maker.data.model.Site
import com.bumptech.glide.Glide

/**
 * app适配器.
 *
 * @author yushaojian
 * @date 2021-05-05 15:10
 */
open class SitesAdapter(@LayoutRes private val itemLayoutId: Int = R.layout.item_site)
    : RecyclerView.Adapter<SitesAdapter.SiteViewHolder>() {

    private val mSites: MutableList<Site> = mutableListOf()

    fun setApps(sites: List<Site>) {
        this.mSites.clear()
        this.mSites.addAll(sites)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SiteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(itemLayoutId, parent, false)
        val appViewHolder = SiteViewHolder(itemView)
        appViewHolder.itemView.setOnClickListener { onAppClick(mSites[appViewHolder.bindingAdapterPosition]) }
        return appViewHolder
    }

    override fun onBindViewHolder(holder: SiteViewHolder, position: Int) {
        holder.bind(mSites[position])
    }

    override fun getItemCount(): Int = mSites.size

    protected open fun onAppClick(site: Site) {}

    class SiteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val titleTV: TextView = itemView.findViewById(R.id.titleTV)
        private val iconIV: ImageView = itemView.findViewById(R.id.iconIV)

        fun bind(site: Site) {
            titleTV.text = site.name
            Glide.with(iconIV.context).load(site.icon).placeholder(R.drawable.ic_site_placeholder_logo).into(iconIV)
        }
    }

}