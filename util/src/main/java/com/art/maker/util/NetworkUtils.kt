@file:Suppress("DEPRECATION")

package com.art.maker.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var activeNetworkInfo: NetworkInfo? = null
    try {
        activeNetworkInfo = connectivityManager.activeNetworkInfo
    } catch (e: Exception) {
        // ignore
    }
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}