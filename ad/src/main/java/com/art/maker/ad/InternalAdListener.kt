package com.art.maker.ad

/**
 * 内部广告事件监听器.
 *
 * 之所以内部不用[AdListener]是为了避免向外部暴露[AdModel]
 *
 * @author yushaojian
 * @date 2021-02-03 16:09
 */
internal interface InternalAdListener {

    /**
     * 广告加载到
     */
    fun onAdLoaded(oid: String, adModel: AdModel) {}

    /**
     * 广告加载失败
     */
    fun onAdLoadError(oid: String, adId: AdId, errorMsg: String) {}

    /**
     * 广告展示
     */
    fun onAdOpened(oid: String) {}

    /**
     * 广告关闭
     */
    fun onAdClosed(oid: String) {}

    /**
     * 获取到激励
     */
    fun onRewardEarned(oid: String) {}
}