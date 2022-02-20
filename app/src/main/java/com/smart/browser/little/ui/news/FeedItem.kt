package com.smart.browser.little.ui.news

import com.art.maker.data.model.NewsArticle
import com.google.android.gms.ads.nativead.NativeAd

/**
 * feed item.
 *
 * @author yushaojian
 * @date 2021-06-17 21:29
 */

sealed class FeedItem

class NewsItem(val article: NewsArticle) : FeedItem()

class AdItem(val nativeAd: NativeAd) : FeedItem()

object PlaceholderItem : FeedItem()