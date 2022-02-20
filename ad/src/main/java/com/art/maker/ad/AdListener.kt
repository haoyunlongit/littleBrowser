package com.art.maker.ad

/**
 * 广告事件回调.
 *
 * @author yushaojian
 * @date 2021-02-03 16:09
 */
open class AdListener {
    var listenerValid = true

    /**
     * 广告加载到
     */
    open fun onAdLoaded(oid: String) {}

    /**
     * 广告加载失败
     */
    open fun onAdLoadError(oid: String, errorMsg: String) {}

    /**
     * 广告展示
     */
    open fun onAdShowed(oid: String) {}

    /**
     * 广告关闭
     */
    open fun onAdClosed(oid: String) {}

    /**
     * 获取到激励
     */
    open fun onRewardEarned(oid: String) {}
}