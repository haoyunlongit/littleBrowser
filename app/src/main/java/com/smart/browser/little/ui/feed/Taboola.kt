package com.smart.browser.little.ui.feed

import com.taboola.android.TBLPublisherInfo
import com.taboola.android.Taboola

/**
 * Taboola相关常量.
 *
 * @author yushaojian
 * @date 2021-07-28 08:56
 */

const val TABOOLA_PUBLISHER = "jmobiletechnology-aiosocialmediabrowser-app-android"
const val TABOOLA_PLACEMENTNAME = "Below Article Thumbnails Feed"
const val TABOOLA_PAGETYPE = "article"
const val TABOOLA_PAGEURL = "https://play.google.com/store/apps/details?id=com.aio.browser.little"
const val TABOOLA_MODE = "thumbnails-a"

fun initTaboola() {
    val publisherInfo = TBLPublisherInfo(TABOOLA_PUBLISHER)
    Taboola.init(publisherInfo)
}