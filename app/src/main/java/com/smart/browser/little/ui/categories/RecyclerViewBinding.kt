package com.smart.browser.little.ui.categories

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smart.browser.little.ui.games.GameItem
import com.smart.browser.little.ui.games.GameSectionAdapter
import com.smart.browser.little.ui.news.FeedAdapter
import com.smart.browser.little.ui.news.FeedItem
import com.art.maker.data.adapter.SectionAdapter
import com.art.maker.data.adapter.SitesAdapter
import com.art.maker.data.model.Site
import com.art.maker.data.model.SiteSection

/**
 * RecyclerView ViewBindings.
 *
 * @author yushaojian
 * @date 2021-05-27 09:44
 */

@BindingAdapter("items")
fun setItems(recyclerView: RecyclerView, items: List<SiteSection>?) {
    items?.let {
        (recyclerView.adapter as SectionAdapter).submitList(items)
    }
}

@BindingAdapter("games")
fun setGames(recyclerView: RecyclerView, items: ArrayList<GameItem>?) {
    items?.let {
        (recyclerView.adapter as GameSectionAdapter).submitList(items)
    }
}

@BindingAdapter("news")
fun setNews(recyclerView: RecyclerView, items: List<FeedItem>?) {
    items?.let {
        (recyclerView.adapter as FeedAdapter).submitList(items as ArrayList<FeedItem>)
    }
}

@BindingAdapter("adapter")
fun setAdapter(recyclerView: RecyclerView, adapter: SectionAdapter) {
    recyclerView.adapter = adapter
}

@BindingAdapter("items")
fun setApps(recyclerView: RecyclerView, items: List<Site>?) {
    items?.let {
        (recyclerView.adapter as SitesAdapter).setApps(items)
    }
}

@BindingAdapter("adapter")
fun setAdapter(recyclerView: RecyclerView, adapter: SitesAdapter) {
    recyclerView.adapter = adapter
}

@BindingAdapter("items")
fun setCategories(recyclerView: RecyclerView, items: List<StatefulCategory>?) {
    items?.let {
        (recyclerView.adapter as AppCategoriesAdapter).submitList(items)
    }
}

@BindingAdapter("adapter")
fun setAdapter(recyclerView: RecyclerView, adapter: AppCategoriesAdapter) {
    recyclerView.adapter = adapter
}