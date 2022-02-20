package com.art.maker.ad

import android.app.Activity
import android.view.ViewGroup

/**
 * 代表一个广告位，加载过程由[adPositionDelegation]实现.
 *
 * @author yushaojian
 * @date 2021-06-09 16:02
 *
 * @param selfOid 此广告位oid
 * @param adPositionDelegation 被共享的广告位
 */
class AdPositionShare(selfOid: String, private val adPositionDelegation: AdPositionContract) :
    AdPositionContract(selfOid) {

    /**
     * 展示过程由自身负责，不通过[adPositionDelegation]，以免将展示、激励和关闭事件发给[adPositionDelegation]的adListeners
     */
    private val listener = object : InternalAdListener {
        override fun onAdOpened(oid: String) {
            Log.d("$selfOid onAdImpression")
            adListeners.forEach { it.onAdShowed(selfOid) }
        }

        override fun onRewardEarned(oid: String) {
            Log.d("$selfOid onRewardEarned")
            adListeners.forEach { it.onRewardEarned(selfOid) }
        }

        override fun onAdClosed(oid: String) {
            Log.d("$selfOid onAdClosed")
            adListeners.forEach { it.onAdClosed(selfOid) }
        }
    }

    private val adListeners = mutableListOf<AdListener>()

    /**
     * 加载过程是委托给[adPositionDelegation]的，所以必须向它注册，接收到回调后转发给自身的[adListeners]
     */
    private val delegationListener = object : AdListener() {
        override fun onAdLoaded(oid: String) {
            Log.d("$selfOid onAdLoaded by $oid")
            adListeners.forEach { it.onAdLoaded(selfOid) }
        }

        override fun onAdLoadError(oid: String, errorMsg: String) {
            Log.d("$selfOid onAdLoadError by $oid")
            adListeners.forEach { it.onAdLoadError(selfOid, errorMsg) }
        }
    }

    override fun addAdLoadListener(listener: AdListener) {
        adListeners.add(listener)
    }

    override fun removeAdLoadListener(listener: AdListener) {
        adListeners.remove(listener)
    }

    override fun removeAdLoadListeners() {
        adListeners.clear()
    }

    /**
     * 加载过程委托给[adPositionDelegation].
     * 为了保证[adPositionDelegation]持有[delegationListener]，每次都重新添加一次
     */
    override fun load(activity: Activity): Boolean {
        Log.i("$oid load delegating to ${adPositionDelegation.oid}")
        adPositionDelegation.removeAdLoadListener(delegationListener)
        adPositionDelegation.addAdLoadListener(delegationListener)
        return adPositionDelegation.load(activity)
    }

    override fun show(activity: Activity): Boolean {
        val adModel: AdModel = popAd() ?: return false
        Log.i("$oid show delegating to ${adPositionDelegation.oid}")
        adModel.show(activity, listener)
        return true
    }

    override fun show(viewGroup: ViewGroup, nativeCustom: NativeCustom?): Destroyable? {
        val adModel: AdModel = adPositionDelegation.popAd() ?: return null
        Log.i("$oid show delegating to ${adPositionDelegation.oid}")
        return adModel.show(viewGroup, nativeCustom, listener)
    }

    override fun isFilled(): Boolean = adPositionDelegation.isFilled()

    override fun hasCache(): Boolean {
        return adPositionDelegation.hasCache()
    }

    override fun popAd(): AdModel? {
        return adPositionDelegation.popAd()
    }

}