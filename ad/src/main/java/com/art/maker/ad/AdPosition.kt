package com.art.maker.ad

import android.app.Activity
import android.view.ViewGroup
import java.lang.ref.WeakReference

/**
 * 代表一个广告位，加载过程由自己实现.
 *
 * 构造器属性是此广告位的配置，代码里是广告的加载、展示和缓存逻辑.
 *
 * 负责管理每个广告位的ids、格式、加载流程、缓存
 */
class AdPosition(
    oid: String,
    private val adIdGroups: List<List<AdId>>,
    count: Int = 1,
    private val refill: Boolean = false
) : AdPositionContract(oid) {

    private val fillCount = if (count < 1) 1 else count
    private var adIdGroupIndex = 0
    private var loadingAdIds = mutableListOf<AdId>()
    private val adListeners = mutableListOf<AdListener>()
    private val adModels = mutableListOf<AdModel>()
    private var activityWeakReference: WeakReference<Activity>? = null

    private val listener = object : InternalAdListener {

        override fun onAdLoaded(oid: String, adModel: AdModel) {
            adModels.add(adModel)
            val index = loadingAdIds.indexOfFirst { it == adModel.adId }
            if (index != -1) loadingAdIds.removeAt(index) // loadingAdIds可能含有多个相同adId引用，所以不能用removeAll

            if (loadingAdIds.isEmpty() || (isFilled() && loadingAdIds.all { it.priority <= adModel.adId.priority })) {
                adIdGroupIndex = 0

                val activity = activityWeakReference?.get()
                if (activity != null && !isFilled()) load(activity) // 如果未填充满，继续填充
                com.art.maker.util.Log.d("reloadAdIfNull", "load")
                adListeners.forEach {
                    it.onAdLoaded(oid)
                }
                clearInValidListeners()
            }
        }

        fun clearInValidListeners() {
            val iterator = adListeners.iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                if (!next.listenerValid) {
                    iterator.remove()
                }
            }
        }

        override fun onAdLoadError(oid: String, adId: AdId, errorMsg: String) {
            val index = loadingAdIds.indexOfFirst { it == adId }
            if (index != -1) loadingAdIds.removeAt(index) // loadingAdIds可能含有多个相同adId引用，所以不能用removeAll

            if (loadingAdIds.isNotEmpty()) {
                // 还有任务在执行，无须回调
                return
            }

            // 没有任务在执行了，说明是本组最后一个id请求失败，这时有4种情况：
            // 1. 已有填充，本adId是最高优先级
            // 2. 已有填充，本adId不是最高优先级
            // 3. 没有填充，但还有更多串行请求可以触发
            // 4. 没有填充，且没有更多串行请求可以触发
            // 各情况处理：
            // 1. 回调成功
            // 2. 无须任何处理
            // 3. 触发下一串行请求
            // 4. 回调失败

            if (isFilled()) {
                // 如果有填充，且是最高优先级最后失败（说明之前都没有回调过onAdLoaded），则回调成功
                val adIds = adIdGroups.getOrNull(adIdGroupIndex)
                val highestPriority = adIds?.getOrNull(0)?.priority == adId.priority
                if (highestPriority) {
                    adListeners.forEach {
                        it.onAdLoaded(oid)
                    }
                    com.art.maker.util.Log.d("reloadAdIfNull", "highestPriority")
                    clearInValidListeners()
                } // else 无须任何处理，因为之前肯定已回调了成功
            } else {
                adIdGroupIndex++
                val hasNext = adIdGroupIndex < adIdGroups.size
                if (!hasNext) adIdGroupIndex = 0

                Log.w("$oid adIdGroupIndex $adIdGroupIndex hasNext $hasNext")
                val activity = activityWeakReference?.get()
                if (activity != null && hasNext) { // 没有填充，但还有更多串行请求可以触发，发起下一串请求
                    load(activity)
                } else {
                    // 没有填充，且没有更多串行请求可以触发，回调失败
                    adListeners.forEach {
                        it.onAdLoadError(oid, errorMsg)
                    }
                    clearInValidListeners()
                }
            }
        }

        override fun onAdOpened(oid: String) {
            adListeners.forEach {
                it.onAdShowed(oid)
            }

            refill()
        }

        override fun onAdClosed(oid: String) {
            adListeners.forEach {
                it.onAdClosed(oid)
            }
        }

        override fun onRewardEarned(oid: String) {
            adListeners.forEach {
                it.onRewardEarned(oid)
            }
        }

    }

    override fun addAdLoadListener(listener: AdListener) {
        val modified = adListeners.add(listener)
        Log.v("$oid addAdLoadListener $listener modified $modified")
    }

    override fun removeAdLoadListener(listener: AdListener) {
        val modified = try {
            adListeners.remove(listener)
        } catch (e: Exception) {
        }
        Log.v("$oid removeAdLoadListener $listener modified $modified")
    }

    override fun removeAdLoadListeners() {
        adListeners.clear()
        Log.v("$oid removeAdLoadListeners")
    }

    // 返回值代表是否发起加载流程
    override fun load(activity: Activity): Boolean {
        Log.i("$oid load start")
        val adIds = adIdGroups.getOrNull(adIdGroupIndex)
        if (adIds.isNullOrEmpty()) {
            Log.w("$oid has no adIds")
            adListeners.forEach {
                it.onAdLoadError(oid, "$oid has no adIds")
            }
            return false
        }

        if (loadingAdIds.isNotEmpty()) {
            Log.w("$oid already loading")
            return false
        }

        val highestPriorityDifferCount = fillCount - getCacheCountWithPriority(adIds[0].priority)
        if (highestPriorityDifferCount > 0) {
            val adLoaders = mutableListOf<AdLoader>()
            for ((index, adId) in adIds.withIndex()) {
                val diffCount = fillCount - getCacheCountWithPriority(adId.priority)
                if (diffCount > 0) {
                    val count =
                        if (index == 0) highestPriorityDifferCount else 1 // 最高优先级补全，其余优先级各补充1个
                    repeat(count) {
                        loadingAdIds.add(adId)
                        adLoaders.add(AdLoader(oid, adId, adId.format, listener))
                    }
                }
            }
            Log.w("$oid load size = ${adLoaders.size}")
            // 在所有加载对象创建后再发起请求，否则可能造成递归调用（如某些情况下，请求立刻失败，发起下一次load）
            adLoaders.forEach { adLoader ->
                adLoader.load(activity)
            }
            activityWeakReference = WeakReference(activity)
            return true
        } else {
            Log.w("$oid already filled ${adModels.size}/$fillCount")
            return false
        }
    }

    override fun show(activity: Activity): Boolean {
        if (hasCache()) {
            adModels.sortByDescending { it.adId.priority }
            val adModel = adModels.find { it.isValid() }
            if (adModel != null) {
                adModels.remove(adModel)
                Log.d("$oid show priority ${adModel.adId.priority}")
                return adModel.show(activity, listener)
            }
        }
        return false
    }

    override fun show(viewGroup: ViewGroup, nativeCustom: NativeCustom?): Destroyable? {
        val loaded = hasCache()
        if (loaded) {
            adModels.sortByDescending { it.adId.priority }
            val adModel = adModels.find { it.isValid() }
            if (adModel != null) {
                Log.d("$oid show priority ${adModel.adId.priority} admodel=${adModel}")
                adModels.remove(adModel)
                return adModel.show(viewGroup, nativeCustom, listener)
            }
        }
        return null
    }

    override fun isFilled() = getCacheCount() >= fillCount

    override fun hasCache(): Boolean {
        adModels.removeAll { !it.isValid() }
        return adModels.any { it.isValid() }
    }

    override fun popAd(): AdModel? {
        adModels.removeAll { !it.isValid() }
        adModels.sortByDescending { it.adId.priority }
        val adModel = adModels.find { it.isValid() }
        adModel?.let {
            adModels.remove(it)
            Log.d("$oid ad popped")
        }
        refill()
        return adModel
    }

    private fun getCacheCount() = adModels.count { it.isValid() }

    private fun getCacheCountWithPriority(priority: Int) =
        adModels.count { adModel -> adModel.isValid() && adModel.adId.priority == priority }

    private fun refill() {
        val activity = activityWeakReference?.get() ?: return
        if (refill) {
            load(activity)
        }
    }

    override fun toString(): String {
        return "AdPosition(oid='$oid', adIdGroups=${adIdGroups}, refill=$refill, fillCount=$fillCount)"
    }

}