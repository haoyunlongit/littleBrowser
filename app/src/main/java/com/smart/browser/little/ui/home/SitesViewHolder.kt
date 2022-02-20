package com.smart.browser.little.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.smart.browser.little.data.R
import com.smart.browser.little.databinding.HomeItemSiteSectionBinding
import com.art.maker.data.adapter.SitesAdapter
import com.art.maker.data.model.Site
import com.art.maker.data.model.SiteSection

/**
 * 分类网站的ViewHolder.
 *
 * @author yushaojian
 * @date 2021-06-17 21:49
 */
class SitesViewHolder(
    private val binding: HomeItemSiteSectionBinding,
    private val unlock: (keyOfCategory: String) -> Unit,
    private val openCategory: (name: String, key: String) -> Unit,
    private val openApp: (site: Site) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private var siteSection: SiteSection? = null

    private val appAdapter = object : SitesAdapter(R.layout.item_site) {
        override fun onAppClick(site: Site) {
            openApp.invoke(site)
        }
    }

    init {
        binding.appsRV.adapter = appAdapter
        binding.unlockTV.setOnClickListener {
            siteSection?.let {
                val key = it.key ?: return@let
                unlock.invoke(key)
            }
        }
        binding.seeAllTV.setOnClickListener {
            siteSection?.let {
                val key = it.key ?: return@let
                openCategory.invoke(it.name, key)
            }
        }
    }

    fun bind(section: SiteSection, unlocked: Boolean) {
        siteSection = section
        binding.titleTV.text = section.name
        val noMore = section.key.isNullOrBlank()
        if (noMore) {
            binding.seeAllTV.isVisible = false
            binding.unlockTV.isVisible = false
        } else {
            if (unlocked) {
                binding.seeAllTV.isVisible = true
                binding.unlockTV.isVisible = false
            } else {
                binding.unlockTV.isVisible = true
                binding.seeAllTV.isVisible = false
            }
        }
        appAdapter.setApps(section.sites)
    }

    companion object {
        fun from(
            parent: ViewGroup,
            unlock: (keyOfCategory: String) -> Unit,
            openCategory: (name: String, key: String) -> Unit,
            openApp: (site: Site) -> Unit
        ): SitesViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = HomeItemSiteSectionBinding.inflate(layoutInflater, parent, false)
            return SitesViewHolder(binding, unlock, openCategory, openApp)
        }
    }
}