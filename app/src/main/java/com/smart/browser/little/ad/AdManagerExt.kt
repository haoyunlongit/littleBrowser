package com.smart.browser.little.ad

import android.app.Activity
import com.art.maker.ad.AdListener
import com.art.maker.ad.AdManager
import com.art.maker.util.Log
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * AdManager拓展.
 *
 * @author yushaojian
 * @date 2021-03-15 16:18
 */

suspend fun AdManager.suspendLoad(activity: Activity, oid: String): Boolean {
    if (!hasOid(oid)) return false // 没有oid配置，快速返回

    if (isFilled(oid)) return true

    var adListener: AdListener? = null
    val loaded: Boolean = suspendCoroutine { continuation ->
        val listener = object : AdListener() {
            override fun onAdLoaded(oid: String) {
                continuation.resume(true)
            }

            override fun onAdLoadError(oid: String, errorMsg: String) {
                continuation.resume(false)
            }
        }
        adListener = listener
        addAdListener(oid, listener)
        val start = load(activity, oid)
        if (!start) {
            continuation.resume(isFilled(oid))
        }
    }
    adListener?.let { removeAdListener(oid, it) }

    return loaded
}


suspend fun AdManager.suspendCancellableLoad(activity: Activity, oid: String): Boolean {
    if (!hasOid(oid)) return false // 没有oid配置，快速返回

    if (hasCache(oid)) return true

    var adListener: AdListener? = null
    Log.i("AdLib", "$oid suspendCancellableLoad start")
    val loaded: Boolean = suspendCancellableCoroutine { continuation ->
        val listener = object : AdListener() {
            override fun onAdLoaded(oid: String) {
                continuation.resume(true)
            }

            override fun onAdLoadError(oid: String, errorMsg: String) {
                continuation.resume(false)
            }
        }
        adListener = listener
        addAdListener(oid, listener)
        val start = load(activity, oid)
        if (!start) {
            continuation.resume(isFilled(oid))
        }
    }
    adListener?.let { removeAdListener(oid, it) }
    Log.i("AdLib", "$oid suspendCancellableLoad end loaded=$loaded")
    return loaded
}