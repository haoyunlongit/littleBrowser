package com.smart.browser.little.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smart.browser.little.R
import com.smart.browser.little.databinding.ItemSearchResultBinding
import com.smart.browser.little.databinding.SearchSuggestionBinding
import com.art.maker.data.model.Site

/**
 * 搜索结果Adapter.
 *
 * @author yushaojian
 * @date 2021-06-20 16:24
 */
class SearchResultAdapter(
    private val openSite: (Site) -> Unit,
    private val openSuggestion: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val results = ArrayList<ResultItem>()

    fun submitList(results: List<ResultItem>) {
        this.results.clear()
        this.results.addAll(results)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (results[position]) {
            is SuggestionItem -> R.layout.search_suggestion
            else -> R.layout.item_search_result
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.search_suggestion -> {
                val searchSuggestionBinding = SearchSuggestionBinding.inflate(inflater, parent, false)
                SearchSuggestionViewHolder(searchSuggestionBinding, openSuggestion)
            }
            else -> {
                val searchResultBinding = ItemSearchResultBinding.inflate(inflater, parent, false)
                SearchResultViewHolder(searchResultBinding, openSite)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = results[position]
        if (item is SiteItem && holder is SearchResultViewHolder) {
            holder.bind(item.site)
        } else if (item is SuggestionItem && holder is SearchSuggestionViewHolder) {
            holder.bind(item.site, item.query)
        }
    }

    override fun getItemCount(): Int = results.size

}