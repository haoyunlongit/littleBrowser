package com.smart.browser.little.ad

import android.content.Context
import androidx.annotation.Keep
import com.smart.browser.little.BuildConfig
import com.smart.browser.little.R
import com.smart.browser.little.config.AD_CONFIG_KEY
import com.smart.browser.little.config.RemoteConfig
import com.art.maker.util.readTextFileFromResource
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// 给广告模块中的类起别名
typealias AdxConfig = com.art.maker.ad.AdConfig
typealias AdxPosition = com.art.maker.ad.AdPosition
typealias AdxId = com.art.maker.ad.AdId

/**
 * 用于解析广告配置文件的纯数据类.
 *
 * @author yushaojian
 * @date 2021-06-13 15:50
 */
@Keep
data class AdConfig(val adPositions: List<AdPosition>?, val adShares: Map<String, String>?)

@Keep
data class AdPosition(
    val oid: String,
    val adIds: List<List<AdId>>,
    val format: Int? = null,
    val count: Int = 1,
    val refill: Int = 0
)

@Keep
data class AdId(
    val format: Int? = null,
    val value: String,
    val priority: Int = 0
)

suspend fun getAdConfig(context: Context): AdxConfig = withContext(Dispatchers.IO) {
    try {
        var config = if (BuildConfig.DEV) "" else RemoteConfig.getString(AD_CONFIG_KEY)
        if (config.isBlank()) {
            config = readTextFileFromResource(context.applicationContext, R.raw.ad_config)
        }

        val adConfig: AdConfig = Gson().fromJson(config, AdConfig::class.java)
        return@withContext adConfig.toAdxConfig()
    } catch (e: Exception) {
        return@withContext AdxConfig(emptyList(), emptyMap())
    }
}

/**
 * 转为广告模块中的类.
 *
 * @author yushaojian
 * @date 2021-07-26 14:13
 */
fun AdConfig.toAdxConfig(): AdxConfig {
    if (adPositions.isNullOrEmpty()) return AdxConfig(emptyList(), emptyMap())

    val adxPositions = ArrayList<AdxPosition>()
    for (position in adPositions) {
        adxPositions.add(position.toAdxPosition() ?: continue)
    }
    return AdxConfig(adxPositions, adShares ?: emptyMap())
}

/**
 * 转为广告模块中的类.
 *
 * @author yushaojian
 * @date 2021-07-26 14:13
 */
fun AdPosition.toAdxPosition(): AdxPosition? {
    val firstDimension = ArrayList<List<AdxId>>()
    for (first in this.adIds) { // 遍历一维
        val secondDimension = ArrayList<AdxId>()
        for (adId in first) { // 遍历二维
            val format = adId.format ?: this.format ?: continue // 如果value级没有配置format，则取oid级format；如果都没有配置，则此配置无效
            secondDimension.add(AdxId(format, adId.value, adId.priority))
        }
        if (secondDimension.isNotEmpty()) firstDimension.add(secondDimension)
    }
    return if (firstDimension.isEmpty()) null else AdxPosition(oid, firstDimension, count, refill != 0)
}