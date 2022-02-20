package com.smart.browser.little.ui.games

import com.art.maker.data.model.SiteSection
import com.google.android.gms.ads.nativead.NativeAd

/**
 * 作者　:  hl
 * 时间　:  2021/12/28
 * 描述　: 游戏页面数据结构
 **/
sealed class GameItem

class SitesItem(val siteSection: SiteSection, var unlocked: Boolean) : GameItem()

class AdItem(val nativeAd: NativeAd) : GameItem()

object PlaceholderItem : GameItem()