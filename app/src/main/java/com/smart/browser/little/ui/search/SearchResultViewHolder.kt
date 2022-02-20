package com.smart.browser.little.ui.search

import androidx.recyclerview.widget.RecyclerView
import com.smart.browser.little.R
import com.smart.browser.little.databinding.ItemSearchResultBinding
import com.art.maker.data.model.Site
import com.bumptech.glide.Glide

/**
 * 搜索建议.
 *
 * @author yushaojian
 * @date 2021-06-20 16:31
 */
class SearchResultViewHolder(
    private val binding: ItemSearchResultBinding,
    private val openSite: (Site) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private var site: Site? = null

    init {
        binding.root.setOnClickListener {
            val site = site ?: return@setOnClickListener
            openSite(site)
        }
    }

    fun bind(site: Site) {
        this.site = site
        binding.titleTV.text = site.name
        Glide.with(binding.root.context).load(site.icon).placeholder(R.drawable.image_place_holder).into(binding.iconIV)
    }
}