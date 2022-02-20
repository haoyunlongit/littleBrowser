package com.smart.browser.little.config

import android.annotation.SuppressLint
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * 远程配置.
 *
 * @author yushaojian
 * @date 2021-06-20 10:51
 */
@Suppress("unused")
object RemoteConfig {

    private const val DEBUG_MIN_FETCH_INTERVAL = 0L // 即时刷新
    private const val RELEASE_MIN_FETCH_INTERVAL = 24 * 60 * 60L // 24h
    private val interval = if (BuildConfig.DEBUG) DEBUG_MIN_FETCH_INTERVAL else RELEASE_MIN_FETCH_INTERVAL

    @SuppressLint("StaticFieldLeak")
    private val remoteConfig = FirebaseRemoteConfig.getInstance().apply {
        setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(interval)
                .build()
        )
    }

    fun setDefaultsAsync(defaults: Map<String, Any>) {
        remoteConfig.setDefaultsAsync(defaults)
    }

    fun fetchAndActivate() {
        remoteConfig.fetchAndActivate()
    }

    suspend fun fetchAndWaite(): Boolean = suspendCoroutine { continuation ->
        remoteConfig.fetchAndActivate().addOnCompleteListener { continuation.resume(it.isSuccessful) }
    }

    fun getInt(key: String): Int {
        return remoteConfig.getLong(key).toInt()
    }

    fun getLong(key: String): Long {
        return remoteConfig.getLong(key)
    }

    fun getString(key: String): String {
        return remoteConfig.getString(key)
    }
}