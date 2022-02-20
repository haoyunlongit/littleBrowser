package com.art.maker.ad

import android.app.Activity
import android.view.ViewGroup

/**
 * 广告管理器，客户端只通过此类加载、展示广告.
 *
 * 广告加载由[AdManager]、[AdPosition]、[AdLoader]共同完成.
 * [AdManager]负责注册、管理广告位，[AdPosition]负责管理每个广告位的ids、格式、加载流程、缓存，[AdLoader]负责加载广告
 *
 *  ______       _________                           ___________                                               _________                      _____
 * |Client|-----|[AdManager]|-----------------------|[AdPosition]|--------------------------------- ----------|[AdLoader]|--------------------|AdMob|
 *  ------       ---------                            ----------                                               --------                        -----
 *                   |                                     |                                                       |                             |
 *                   |                                     |                   n ---- load(adId1, adFormat) --->   | load(adId1, adFormat) --->  |
 *                   |                                     |                  /                                    |                             |
 *                   |               y ---- load(oid) ---> | loaded or loading           failed                    |            <------- failed  |
 *                   |              /                      |                  \            | (has more ad id)      |                             |
 *  load(oid) ---->  | hasAdPosition                       |                   y           v                       |                             |
 *                   |              \                      |                          load(adId2, adFormat) --->   | load(adId1, adFormat) --->  |
 *                   |               n                     |                                                       |                             |
 *                   |                                     |                             failed                    |            <------- failed  |
 *                   |    notify error     <----------     |   deliver error    <------    | (no more ad id)       |                             |
 *                   |                                     |                                                       |                             |
 *                   |    notify success   <----------     |   deliver success  <------ cache <------ success      |            <------ success  |
 *                   |                                     |                                                       |                             |
 *
 */
@Suppress("unused")
object AdManager {

    var adReporter: AdReporter? = null

    private val adPositions = mutableListOf<AdPositionContract>()
    private val blockedOids = mutableListOf<String>()
    private val nativeCustoms = mutableMapOf<String, NativeCustom>() // oid -> NativeCustom

    fun isAdConfigSet() = adPositions.isNotEmpty()

    fun setAdConfig(adConfig: AdConfig) {
        Log.i("setAdConfig")
        if (isAdConfigSet()) {
            Log.w("setAdConfig been called, skip overriding")
            return
        }

        this.adPositions.addAll(adConfig.adPositions)

        for ((masterOid, delegationOid) in adConfig.adShares) {
            val adPosition = adPositions.find { it.oid == delegationOid } ?: continue
            this.adPositions.add((AdPositionShare(masterOid, adPosition)))
        }
    }

    fun addAdListener(oid: String, adListener: AdListener) {
        val adPosition = adPositions.find { it.oid == oid } ?: return
        adPosition.addAdLoadListener(adListener)
    }

    fun removeAdListener(oid: String, adListener: AdListener) {
        val adPosition = adPositions.find { it.oid == oid } ?: return
        adPosition.removeAdLoadListener(adListener)
    }

    fun removeAdListeners(oid: String) {
        val adPosition = adPositions.find { it.oid == oid } ?: return
        adPosition.removeAdLoadListeners()
    }

    fun load(activity: Activity, oid: String): Boolean {
        val adPosition = adPositions.find { it.oid == oid } ?: return false
        return adPosition.load(activity)
    }

    fun loadWithCallback(activity: Activity, oid: String,adListener: AdListener): Boolean {
        val adPosition = adPositions.find { it.oid == oid } ?: return false
        adPosition.addAdLoadListener(adListener)
        return adPosition.load(activity)
    }

    fun show(activity: Activity, oid: String): Boolean {
        if (isShowBlocked(oid)) {
            Log.i("$oid blocked, skipping show")
            return false
        }

        val adPosition = adPositions.find { it.oid == oid } ?: return false
        return adPosition.show(activity)
    }

    fun show(viewGroup: ViewGroup, oid: String): Destroyable? {
        if (isShowBlocked(oid)) {
            return null
        }

        val adPosition = adPositions.find { it.oid == oid } ?: return null
        return adPosition.show(viewGroup, getNativeCustom(oid))
    }

    fun registerNativeCustom(oid: String, nativeCustom: NativeCustom) {
        nativeCustoms[oid] = nativeCustom
    }

    fun unregisterNativeCustom(oid: String) {
        nativeCustoms.remove(oid)
    }

    fun hasNativeCustom(oid: String) = getNativeCustom(oid) != null

    internal fun getNativeCustom(oid: String): NativeCustom? {
        return nativeCustoms[oid]
    }

    fun blockShow(oid: String) {
        if (!isShowBlocked(oid)) {
            Log.i("$oid blockShow")
            blockedOids.add(oid)
        }
    }

    fun cancelBlockShow(oid: String) {
        if (isShowBlocked(oid)) {
            Log.i("$oid cancelBlockShow")
            blockedOids.remove(oid)
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun isShowBlocked(oid: String) = blockedOids.contains(oid)

    fun isFilled(oid: String): Boolean {
        val adPosition = adPositions.find { it.oid == oid } ?: return false
        return adPosition.isFilled()
    }

    fun hasCache(oid: String): Boolean {
        val adPosition = adPositions.find { it.oid == oid } ?: return false
        return adPosition.hasCache()
    }

    fun hasOid(oid: String): Boolean {
        return adPositions.any { it.oid == oid }
    }

    fun getAd(oid: String): Any? {
        val adPosition = adPositions.find { it.oid == oid } ?: return null
        return adPosition.popAd()?.getAd()
    }

    fun debugAd(enabled: Boolean) {
        Log.enabled = enabled
    }
}