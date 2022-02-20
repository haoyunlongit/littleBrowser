package com.smart.browser.little.config

import android.text.format.DateUtils
import androidx.collection.ArrayMap

// ------------------------------------------------------------------
// ---------------------- 启动页配置相关常量 -----------------------------
// ------------------------------------------------------------------

const val MIN_SPLASH_DURATION = DateUtils.SECOND_IN_MILLIS
const val MAX_SPLASH_DURATION_KEY = "max_splash_duration"
const val MAX_SPLASH_DURATION_DEFAULT = 8L


// ------------------------------------------------------------------
// ---------------------- 广告配置相关常量 -----------------------------
// ------------------------------------------------------------------

const val AD_CONFIG_KEY = "ad_config"

// app open广告
const val APP_OPEN_AD_INTERVAL_KEY = "app_open_ad_interval"
private const val APP_OPEN_AD_INTERVAL_DEFAULT = 60 // 60s

// 网站进入广告
const val SITE_ENTER_INTER_OFFSET_KEY = "site_enter_i_offset"
const val SITE_ENTER_INTER_PERIOD_KEY = "site_enter_i_period"
private const val SITE_ENTER_INTER_PERIOD_DEFAULT = 4

// 网站退出广告
const val SITE_EXIT_AD_INTERVAL_KEY = "site_enter_ad_interval"
private const val SITE_EXIT_AD_PERIOD_DEFAULT = 10

// 信息流原生广告
const val HOME_FEED_AD_PERIOD_KEY = "home_feed_ad_period"
private const val HOME_FEED_AD_PERIOD_DEFAULT = 2
const val HOME_FEED_AD_MAX_COUNT_KEY = "home_feed_ad_max_count"
private const val HOME_FEED_AD_MAX_COUNT_DEFAULT = 3


// 游戏原生广告
const val HOME_GAME_AD_PERIOD_KEY = "home_game_ad_period"
private const val HOME_GAME_AD_PERIOD_DEFAULT = 2
const val HOME_GAME_AD_MAX_COUNT_KEY = "home_game_ad_max_count"
private const val HOME_GAME_AD_MAX_COUNT_DEFAULT = 3

// 新闻原生广告
const val HOME_NEWS_FEED_AD_PERIOD_KEY = "home_news_feed_ad_period"
private const val HOME_NEWS_FEED_AD_PERIOD_DEFAULT = 2
const val HOME_NEWS_FEED_AD_MAX_COUNT_KEY = "home_news_feed_ad_max_count"
private const val HOME_NEWS_FEED_AD_MAX_COUNT_DEFAULT = 100

//主页和游戏页面显示N排图标
const val HOME_ICON_ROW_COUNT = "home_icon_row_count"
private const val HOME_ICON_ROW_COUNT_DEFAULT = 2

val defaultRemoteConfigs = ArrayMap<String, Any>().apply {
    put(MAX_SPLASH_DURATION_KEY, MAX_SPLASH_DURATION_DEFAULT)

    put(APP_OPEN_AD_INTERVAL_KEY, APP_OPEN_AD_INTERVAL_DEFAULT)

    put(SITE_ENTER_INTER_PERIOD_KEY, SITE_ENTER_INTER_PERIOD_DEFAULT)
    put(SITE_EXIT_AD_INTERVAL_KEY, SITE_EXIT_AD_PERIOD_DEFAULT)

    put(HOME_FEED_AD_PERIOD_KEY, HOME_FEED_AD_PERIOD_DEFAULT)
    put(HOME_FEED_AD_MAX_COUNT_KEY, HOME_FEED_AD_MAX_COUNT_DEFAULT)

    put(HOME_GAME_AD_PERIOD_KEY, HOME_GAME_AD_PERIOD_DEFAULT)
    put(HOME_GAME_AD_MAX_COUNT_KEY, HOME_GAME_AD_MAX_COUNT_DEFAULT)

    put(HOME_NEWS_FEED_AD_PERIOD_KEY, HOME_NEWS_FEED_AD_PERIOD_DEFAULT)
    put(HOME_NEWS_FEED_AD_MAX_COUNT_KEY, HOME_NEWS_FEED_AD_MAX_COUNT_DEFAULT)

    put(HOME_ICON_ROW_COUNT, HOME_ICON_ROW_COUNT_DEFAULT)
}

const val AB_SWITCH_OPEN = "1"
const val AB_VIDEO_DOWNLOAD_SWITCH = "video_download_switch"