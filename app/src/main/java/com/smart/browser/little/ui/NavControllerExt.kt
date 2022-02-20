package com.smart.browser.little.ui

import androidx.activity.ComponentActivity
import androidx.navigation.NavController
import com.smart.browser.little.NavGraphDirections
import com.smart.browser.little.ad.SiteEnterAd
import com.art.maker.data.model.Site

/**
 * NavController拓展.
 *
 * @author yushaojian
 * @date 2021-06-16 20:22
 */

fun NavController.viewSite(activity: ComponentActivity, site: Site) {
    SiteEnterAd.show(activity)
    navigate(NavGraphDirections.toAppViewFragment(site.name, site))
}

fun NavController.viewGame(activity: ComponentActivity, site: Site) {
    SiteEnterAd.show(activity)
    navigate(NavGraphDirections.toGameViewFragment(site.name, site))
}