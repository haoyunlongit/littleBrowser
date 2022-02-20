package com.smart.browser.little.report

import android.os.Bundle
import androidx.core.os.bundleOf

/**
 * 事件上报器，所有app事件都通过此类转发，以使事件上报统一管理.
 */

fun reportPageShow(layout: String) {
    report(layout, ITEM_SHOW)
}

fun reportCategoryClick(layout: String, name: String) {
    val extra = bundleOf(EXTRA_NAME to name)
    report(layout, ITEM_CATEGORY_CLICK, extra, mapOf(EXTRA_NAME to name))
}

fun reportAppClick(layout: String, name: String) {
    val extra = bundleOf(EXTRA_NAME to name)
    report(layout, ITEM_APP_CLICK, extra, mapOf(EXTRA_NAME to name))
}

fun reportGameClick(layout: String, name: String) {
    val extra = bundleOf(EXTRA_NAME to name)
    report(layout, ITEM_GAME_CLICK, extra, mapOf(EXTRA_NAME to name))
}

fun reportEvent(action: String, type: String? = "", page: String? = "", extra: String? = ""){
    val param = Bundle()
    param.putString("ACTION", action)
    param.putString("TYPE", type?:"")
    param.putString("PAGE", page?:"")
    param.putString("EXTRA", extra?:"")
    val map = mapOf("ACTION" to action, "TYPE" to (type?:""), "PAGE" to (page?:""), "EXTRA" to (extra?:""))
    report(action, param, map)
}

fun reportAdImpressionScene(oid: String) {
    val extra = Bundle()
    extra.putString("ad_scene", oid)
    val map = mapOf("ad_scene" to oid)
    report("ad_impression_scene", extra, map)
}

fun reportMarketEvent(action: String, fbAction: String){
    reportMarket(action, fbAction)
}