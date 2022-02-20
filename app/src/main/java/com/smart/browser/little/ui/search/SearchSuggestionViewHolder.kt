package com.smart.browser.little.ui.search

import androidx.recyclerview.widget.RecyclerView
import com.smart.browser.little.R
import com.smart.browser.little.databinding.SearchSuggestionBinding
import com.art.maker.data.model.Site
import com.bumptech.glide.Glide

/**
 * 搜索建议.
 *
 * @author yushaojian
 * @date 2021-06-20 16:31
 */
class SearchSuggestionViewHolder(
    private val binding: SearchSuggestionBinding,
    private val openSuggestion: (String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private var site: Site? = null
    private var query: String? = null

    init {
        binding.root.setOnClickListener {
            val site = site ?: return@setOnClickListener
            val query = query ?: return@setOnClickListener
            val searchUrl = "${site.url}$query"
            openSuggestion(searchUrl)
        }
    }

    fun bind(site: Site, query: String) {
        this.site = site
        this.query = query
        val context = binding.root.context
        Glide.with(context).load(site.icon).placeholder(R.drawable.image_place_holder).into(binding.iconIV)
    }
}