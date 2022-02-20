package com.smart.browser.little.report

import android.os.Bundle
import com.smart.browser.little.BrowserApplication
import com.smart.browser.little.BuildConfig
import com.art.maker.util.Log
import com.facebook.appevents.AppEventsLogger
import com.flurry.android.FlurryAgent
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.lang.Exception

private val firebaseAnalytics by lazy { FirebaseAnalytics.getInstance(BrowserApplication.context) }
private val sAppEventsLogger by lazy { AppEventsLogger.newLogger(BrowserApplication.context) }

fun report(layout: String, item: String, extra: Bundle? = null, map: Map<String, String>? = null) {
    if (BuildConfig.DEBUG) {
        Log.d("EventReporter", "$layout $item $extra")
        return
    }
    firebaseAnalytics.logEvent(layout + "_" + item, extra)
    if(map == null){
        FlurryAgent.logEvent(layout + "_" + item)
    }else{
        FlurryAgent.logEvent(layout + "_" + item, map)
    }
}

fun report(action: String, param: Bundle? = null, map: Map<String, String>? = null) {
    if (BuildConfig.DEBUG) {
        Log.d("EventReporter", "report action=$action, param=$param")
        return
    }
    firebaseAnalytics.logEvent(action, param)
    if(map == null){
        FlurryAgent.logEvent(action)
    }else{
        FlurryAgent.logEvent(action, map)
    }
}

fun reportMarket(action: String, fbAction: String) {
    if (BuildConfig.DEBUG) {
        Log.d("EventReporter", "reportMarketEvent action=$action,fbAction=$fbAction")
        return
    }
    firebaseAnalytics.logEvent(action, null)
    sAppEventsLogger.logEvent(fbAction)
    FlurryAgent.logEvent(action)
}

fun reportCustomException(message: String){
    FirebaseCrashlytics.getInstance().recordException(Exception(message))
}
