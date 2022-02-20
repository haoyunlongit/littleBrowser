package com.smart.browser.little.ui.home

import com.art.maker.data.model.SiteSection
import com.google.android.gms.ads.nativead.NativeAd

/**
 * 主页信息流item.
 *
 * @author yushaojian
 * @date 2021-06-17 21:29
 */

sealed class HomeItem

class SitesItem(val siteSection: SiteSection, var unlocked: Boolean) : HomeItem()

class AdItem(val nativeAd: NativeAd) : HomeItem()

object PlaceholderItem : HomeItem()