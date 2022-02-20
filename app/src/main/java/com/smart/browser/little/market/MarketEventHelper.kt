package com.smart.browser.little.market

import com.smart.browser.little.BrowserApplication
import com.smart.browser.little.ad.*
import com.smart.browser.little.report.reportMarketEvent
import com.art.maker.ad.AdFormat
import com.art.maker.ad.AdFormat.Companion.APP_OPEN
import com.art.maker.ad.AdFormat.Companion.INTERSTITIAL
import com.art.maker.ad.AdFormat.Companion.NATIVE_INTERSTITIAL
import com.art.maker.ad.AdFormat.Companion.REWARD_INTERSTITIAL
import com.art.maker.ad.AdFormat.Companion.REWARD_VIDEO
import com.art.maker.util.Log
import com.art.maker.util.SPUtils
import com.art.maker.util.getProcessName
import com.facebook.appevents.AppEventsConstants
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.*

object MarketEventHelper {
    const val TAG = "MarketEventHelper"
    const val TIME_LIMIT = 24 * 60 * 60 * 1000L
    const val DAY_IN_TIME_MILLIS = 24 * 60 * 60 * 1000L
    const val SP_APP_INSTALLED_TIMESTAMP = "sp_app_installed_timestamp"

    //广告展示十次是否上传过
    const val SP_HAVE_UPLOAD_AD_SHOW_TEN_TIMES = "sp_have_upload_ad_show_ten_times"

    //广告展示次数
    const val SP_AD_SHOW_TIMES = "sp_ad_show_times"

    //功能使用十次是否上传过
    const val SP_HAVE_UPLOAD_FUNCTION_USE_TEN_TIMES = "sp_have_upload_function_use_ten_times"

    //功能使用次数
    const val SP_FUNCTION_USE_TEN_TIMES = "sp_function_use_ten_times"

    //启动6次是否上传过
    const val SP_HAVE_UPLOAD_APP_START_SIX_TIMES = "sp_have_upload_app_start_six_times"

    //启动次数
    const val SP_APP_START_TIMES = "sp_app_start_times"

    //VPN授权是否上传过
    const val SP_HAVE_UPLOAD_VPN_AUTHORIZATION = "sp_have_upload_vpn_authorization"

    //次日留存事件是否上传过
    const val SP_HAVE_UPLOAD_SECOND_DAY_RETENTION = "sp_have_upload_second_day_retention"

    //开屏广告展示次数事件是否上传过
    const val SP_HAVE_UPLOAD_SPLASH_AD_SHOW_TIMES = "sp_have_upload_splash_ad_show_times"

    //开屏广告展示次数
    const val SP_SPLASH_AD_SHOW_TIMES = "sp_splash_ad_show_times"

    //广告展示十次
    const val ACTION_MARKET_EVENT_AD_SHOW_TEN_TIMES = "market_event_ad_show_ten_times"

    //达到等级/完成关卡
    const val ACTION_FB_MARKET_EVENT_AD_SHOW_TEN_TIMES =
        AppEventsConstants.EVENT_NAME_ACHIEVED_LEVEL

    // 功能使用十次
    const val ACTION_MARKET_EVENT_FUNCTION_USE_TEN_TIMES = "market_event_function_use_ten_times"

    //完成教程学习
    const val ACTION_FB_MARKET_EVENT_FUNCTION_USE_TEN_TIMES =
        AppEventsConstants.EVENT_NAME_COMPLETED_TUTORIAL

    //启动6次
    const val ACTION_MARKET_EVENT_APP_START_SIX_TIMES = "market_event_app_start_six_times"

    //开始试用
    const val ACTION_FB_MARKET_EVENT_APP_START_SIX_TIMES = AppEventsConstants.EVENT_NAME_START_TRIAL

    //VPN授权
    const val ACTION_MARKET_EVENT_VPN_AUTHORIZATION = "market_event_vpn_authorization"

    //订阅
    const val ACTION_FB_MARKET_EVENT_VPN_AUTHORIZATION = AppEventsConstants.EVENT_NAME_SUBSCRIBE

    //次日留存绑定购买事件
    const val ACTION_MARKET_EVENT_SECOND_DAY_RETENTION = FirebaseAnalytics.Event.PURCHASE

    //FB次日留存
    const val ACTION_FB_MARKET_EVENT_SECOND_DAY_RETENTION = AppEventsConstants.EVENT_NAME_PURCHASED

    //开屏广告展示次数事件
    const val ACTION_MARKET_EVENT_SPLASH_AD_SHOW_TIMES = "market_event_splash_ad_show_times"

    //FB开屏广告展示次数事件
    const val ACTION_FB_MARKET_EVENT_SPLASH_AD_SHOW_TIMES = AppEventsConstants.EVENT_NAME_DONATE

    fun recordAppInstalledTime() {
        val processName = getProcessName(BrowserApplication.context)
        if (BrowserApplication.context.packageName == processName) {
            val appInstalledTimeStamp = SPUtils.getLong(SP_APP_INSTALLED_TIMESTAMP, 0)
            if (appInstalledTimeStamp == 0L) {
                SPUtils.putLong(SP_APP_INSTALLED_TIMESTAMP, System.currentTimeMillis())
            }
        }
    }

    fun getAppInstalledTime(): Long {
        return SPUtils.getLong(SP_APP_INSTALLED_TIMESTAMP, 0)
    }

    fun onAdShow(oid: String, @AdFormat format: Int) {
        Log.d(TAG, "onAdShow oid=$oid format=$format")
        if (INTERSTITIAL != format
            && REWARD_VIDEO != format
            && APP_OPEN != format
            && REWARD_INTERSTITIAL != format
            && NATIVE_INTERSTITIAL != format
        ) {
            Log.d(TAG, "onAdShow return because format not match")
            return
        }
        val haveUpload =
            SPUtils.getBoolean(SP_HAVE_UPLOAD_AD_SHOW_TEN_TIMES, false)
        if (haveUpload) {
            Log.d(TAG, "onAdShow haveUpload")
            return
        }
        val appInstalledTimeStamp = getAppInstalledTime()
        if (System.currentTimeMillis() - appInstalledTimeStamp > TIME_LIMIT) {
            Log.d(TAG, "onAdShow time out")
            return
        }
        var adShowTimes = SPUtils.getInt(SP_AD_SHOW_TIMES, 0)
        adShowTimes++
        Log.d(TAG, "onAdShow adShowTimes=$adShowTimes")
        if (adShowTimes == 10) {
            //upload
            SPUtils.putBoolean(SP_HAVE_UPLOAD_AD_SHOW_TEN_TIMES, true)
            reportMarketEvent(
                ACTION_MARKET_EVENT_AD_SHOW_TEN_TIMES,
                ACTION_FB_MARKET_EVENT_AD_SHOW_TEN_TIMES
            )
            Log.d(TAG, "onAdShow upload ten times")
        }
        SPUtils.putInt(SP_AD_SHOW_TIMES, adShowTimes)
    }

    fun onFunctionUseComplete() {
        Log.d(TAG, "onFunctionUseComplete")
        val haveUpload =
            SPUtils.getBoolean(SP_HAVE_UPLOAD_FUNCTION_USE_TEN_TIMES, false)
        if (haveUpload) {
            Log.d(TAG, "onFunctionUseComplete haveUpload")
            return
        }
        val appInstalledTimeStamp = getAppInstalledTime()
        if (System.currentTimeMillis() - appInstalledTimeStamp > TIME_LIMIT) {
            Log.d(TAG, "onFunctionUseComplete time out")
            return
        }
        var functionUseTimes = SPUtils.getInt(SP_FUNCTION_USE_TEN_TIMES, 0)
        functionUseTimes++
        Log.d(TAG, "onFunctionUseComplete functionUseTimes=$functionUseTimes")
        if (functionUseTimes == 10) {
            //upload
            SPUtils.putBoolean(SP_HAVE_UPLOAD_FUNCTION_USE_TEN_TIMES, true)
            reportMarketEvent(
                ACTION_MARKET_EVENT_FUNCTION_USE_TEN_TIMES,
                ACTION_FB_MARKET_EVENT_FUNCTION_USE_TEN_TIMES
            )
            Log.d(TAG, "onFunctionUseComplete upload ten times")
        }
        SPUtils.putInt(SP_FUNCTION_USE_TEN_TIMES, functionUseTimes)
    }

    fun onAppStart() {
        Log.d(TAG, "onAppStart")
        val haveUpload =
            SPUtils.getBoolean(SP_HAVE_UPLOAD_APP_START_SIX_TIMES, false)
        if (haveUpload) {
            Log.d(TAG, "onAppStart haveUpload")
            return
        }
        val appInstalledTimeStamp = getAppInstalledTime()
        if (System.currentTimeMillis() - appInstalledTimeStamp > TIME_LIMIT) {
            Log.d(TAG, "onAppStart time out")
            return
        }
        var appStartTimes = SPUtils.getInt(SP_APP_START_TIMES, 0)
        appStartTimes++
        Log.d(TAG, "onAppStart appStartTimes=$appStartTimes")
        if (appStartTimes == 6) {
            //upload
            SPUtils.putBoolean(SP_HAVE_UPLOAD_APP_START_SIX_TIMES, true)
            reportMarketEvent(
                ACTION_MARKET_EVENT_APP_START_SIX_TIMES,
                ACTION_FB_MARKET_EVENT_APP_START_SIX_TIMES
            )
            Log.d(TAG, "onAppStart upload six time")
        }
        SPUtils.putInt(SP_APP_START_TIMES, appStartTimes)
    }

    fun onProxyAuthorization() {
        Log.d(TAG, "onVpnAuthorization")
        val haveUpload =
            SPUtils.getBoolean(SP_HAVE_UPLOAD_VPN_AUTHORIZATION, false)
        if (haveUpload) {
            Log.d(TAG, "onVpnAuthorization haveUpload")
            return
        }
        val appInstalledTimeStamp = getAppInstalledTime()
        if (System.currentTimeMillis() - appInstalledTimeStamp > TIME_LIMIT) {
            Log.d(TAG, "onVpnAuthorization time out")
            return
        }
        SPUtils.putBoolean(SP_HAVE_UPLOAD_VPN_AUTHORIZATION, true)
        reportMarketEvent(
            ACTION_MARKET_EVENT_VPN_AUTHORIZATION,
            ACTION_FB_MARKET_EVENT_VPN_AUTHORIZATION
        )
        Log.d(TAG, "onVpnAuthorization upload")
    }

    /**
     * 第二日留存事件绑定purchase事件
     */
    fun onSecondDayRetention() {
        Log.d(TAG, "onSecondDayRetention")
        val haveUpload =
            SPUtils.getBoolean(SP_HAVE_UPLOAD_SECOND_DAY_RETENTION, false)
        if (haveUpload) {
            Log.d(TAG, "onSecondDayRetention haveUpload")
            return
        }
        val appInstalledTimeStamp = getAppInstalledTime()
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis > appInstalledTimeStamp + DAY_IN_TIME_MILLIS * 2) {
            Log.d(
                TAG,
                "onSecondDayRetention time more than 48h appInstalledTimeStamp=$appInstalledTimeStamp"
            )
            return
        }
        val appInstalledZeroTimeStamp = getZeroTimeStamp(appInstalledTimeStamp)

        if (currentTimeMillis < appInstalledZeroTimeStamp + DAY_IN_TIME_MILLIS ||
            currentTimeMillis > appInstalledZeroTimeStamp + DAY_IN_TIME_MILLIS * 2
        ) {
            Log.d(
                TAG,
                "onSecondDayRetention time out appInstalledTimeStamp=$appInstalledTimeStamp, next=${appInstalledZeroTimeStamp + DAY_IN_TIME_MILLIS} " +
                        " next2=${appInstalledZeroTimeStamp + DAY_IN_TIME_MILLIS * 2}"
            )
            return
        }

        SPUtils.putBoolean(SP_HAVE_UPLOAD_SECOND_DAY_RETENTION, true)
        reportMarketEvent(
            ACTION_MARKET_EVENT_SECOND_DAY_RETENTION,
            ACTION_FB_MARKET_EVENT_SECOND_DAY_RETENTION
        )
        Log.d(TAG, "onSecondDayRetention upload")
    }

    /**
     * 开屏广告展示次数事件(目前是3次)
     */
    fun onSplashAdShow(oid: String, @AdFormat format: Int) {
        Log.d(TAG, "onSplashAdShow oid=$oid format=$format")
        if (OID_APP_OPEN != oid && OID_SPLASH_INTER != oid && OID_SPLASH_NATIVE_INTER != oid) {
            Log.d(TAG, "onSplashAdShow return because oid not match")
            return
        }
        val haveUpload =
            SPUtils.getBoolean(SP_HAVE_UPLOAD_SPLASH_AD_SHOW_TIMES, false)
        if (haveUpload) {
            Log.d(TAG, "onSplashAdShow haveUpload")
            return
        }
        val appInstalledTimeStamp = getAppInstalledTime()
        if (System.currentTimeMillis() - appInstalledTimeStamp > TIME_LIMIT) {
            Log.d(TAG, "onSplashAdShow time out")
            return
        }
        var adShowTimes = SPUtils.getInt(SP_SPLASH_AD_SHOW_TIMES, 0)
        adShowTimes++
        Log.d(TAG, "onSplashAdShow adShowTimes=$adShowTimes")
        if (adShowTimes == 3) {
            //upload
            SPUtils.putBoolean(SP_HAVE_UPLOAD_SPLASH_AD_SHOW_TIMES, true)
            reportMarketEvent(
                ACTION_MARKET_EVENT_SPLASH_AD_SHOW_TIMES,
                ACTION_FB_MARKET_EVENT_SPLASH_AD_SHOW_TIMES
            )
            Log.d(TAG, "onSplashAdShow upload 3 times")
        }
        SPUtils.putInt(SP_SPLASH_AD_SHOW_TIMES, adShowTimes)
    }


    /**
     * 获取当日零点时间戳
     */
    fun getZeroTimeStamp(timeStamp: Long): Long {
        return timeStamp - ((timeStamp + TimeZone.getDefault().rawOffset) % (24 * 60 * 60 * 1000L))
    }

}