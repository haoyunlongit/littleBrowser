package com.smart.browser.little.ui.news

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.smart.browser.little.R
import com.smart.browser.little.ad.FeedNativeAdViewHolder
import com.smart.browser.little.ui.home.PlaceholderViewHolder

/**
 * 主页Adapter.
 *
 * @author yushaojian
 * @date 2021-06-17 21:40
 */
class FeedAdapter(val fragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var feedItems = ArrayList<FeedItem>()

    fun getItem(position: Int) = feedItems[position]

    fun submitList(feedItems: ArrayList<FeedItem>) {
        this.feedItems = feedItems
        notifyDataSetChanged()
    }

    fun replace(position: Int, FeedItem: FeedItem) {
        if (position < 0 || position >= itemCount) return
        feedItems[position] = FeedItem
        notifyItemChanged(position)
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = feedItems[position]) {
            is NewsItem -> {
                when {
                    !item.article.xlargeThumbnailURL.isNullOrEmpty() -> R.layout.feed_item_large
                    !item.article.largeThumbnailURL.isNullOrEmpty() -> R.layout.feed_item_middle
                    !item.article.thumbnailURL.isNullOrEmpty() -> R.layout.feed_item_small
                    else ->
                        R.layout.feed_item_middle
                }
            }
            is AdItem -> R.layout.ad_native_feed
            is PlaceholderItem -> R.layout.item_placeholder
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == R.layout.ad_native_feed) return FeedNativeAdViewHolder.from(parent)
        if (viewType == R.layout.feed_item_small) return FeedSmallViewHolder.from(parent)
        if (viewType == R.layout.feed_item_middle) return FeedMiddleViewHolder.from(parent)
        if (viewType == R.layout.feed_item_large) return FeedLargeViewHolder.from(parent)
        return PlaceholderViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = feedItems[position]
        if (item is AdItem && holder is FeedNativeAdViewHolder) {
            holder.bind(item.nativeAd)
        } else if (item is NewsItem
            && (holder is BaseFeedViewHolder)
        ) {
            holder.bind(item.article)
        } else if (item is PlaceholderItem) {
            //placeholder
        }
    }

    override fun getItemCount(): Int = feedItems.size

}