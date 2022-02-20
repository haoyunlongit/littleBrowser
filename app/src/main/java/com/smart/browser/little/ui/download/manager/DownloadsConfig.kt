package com.smart.browser.little.ui.download.manager

import com.smart.browser.little.config.AB_SWITCH_OPEN
import com.smart.browser.little.config.AB_VIDEO_DOWNLOAD_SWITCH
import com.smart.browser.little.config.RemoteConfig

fun isVideoDownloadABEnabled() = /*BuildConfig.DEBUG ||*/ RemoteConfig.getString(AB_VIDEO_DOWNLOAD_SWITCH) == AB_SWITCH_OPEN