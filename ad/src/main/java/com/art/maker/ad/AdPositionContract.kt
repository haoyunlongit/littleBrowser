package com.art.maker.ad

import android.app.Activity
import android.view.ViewGroup

/**
 * 定义一个广告位的所有外部API.
 *
 * 负责管理每个广告位的ids、格式、加载流程、缓存
 *
 * @author yushaojian
 * @date 2021-06-09 16:19
 */
abstract class AdPositionContract(val oid: String) {

    abstract fun addAdLoadListener(listener: AdListener)

    abstract fun removeAdLoadListener(listener: AdListener)

    abstract fun removeAdLoadListeners()

    abstract fun load(activity: Activity): Boolean

    abstract fun show(activity: Activity): Boolean

    abstract fun show(viewGroup: ViewGroup, nativeCustom: NativeCustom? = null): Destroyable?

    abstract fun isFilled(): Boolean

    abstract fun hasCache(): Boolean

    internal abstract fun popAd(): AdModel?
}