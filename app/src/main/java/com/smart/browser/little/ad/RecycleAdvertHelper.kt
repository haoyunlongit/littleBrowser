package com.smart.browser.little.ad

import android.app.Activity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smart.browser.little.report.reportCustomException
import com.art.maker.ad.AdListener
import com.art.maker.util.Log
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 作者　:  hl
 * 时间　:  2021/12/28
 * 描述　:
 **/

class RecycleAdvertHelper {

    inline fun <reified PlaceholderItem, AdItem> reLoadAdByScroll(
        activity: Activity?,
        recyclerView: RecyclerView,
        newState: Int,
        itemCount: Int,
        getItem: (pos: Int) -> Any?,
        fetchAdvert: () -> Any?,
        loadAdvert: (activity: Activity) -> Unit,
        createAdItem: (ad: NativeAd) -> AdItem,
        updateItemAd: (pos: Int, adItem: AdItem) -> Unit
    ) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            // 将占位item替换为广告
            replacePlaceholderWithAd<PlaceholderItem, AdItem>(
                recyclerView,
                itemCount,
                getItem,
                fetchAdvert,
                createAdItem,
                updateItemAd
            )
        }

        /*if (newState == RecyclerView.SCROLL_STATE_SETTLING && hasPlaceholder<PlaceholderItem>(
                itemCount,
                getItem
            )
        ) {
            val act = activity ?: return
            loadAdvert(act)
        }*/
    }


    // 列表中是否还有广告占位item
    inline fun <reified PlaceholderItem> hasPlaceholder(
        itemCount: Int,
        getItem: (pos: Int) -> Any?,
    ): Boolean {
        val start = 0
        val end = itemCount - 1
        for (position in start..end) {
            if (position < 0 || position >= itemCount) continue
            if (getItem(position) is PlaceholderItem) return true
        }
        return false
    }

    inline fun <reified PlaceholderItem, AdItem> replacePlaceholderWithAd(
        recyclerView: RecyclerView,
        itemCount: Int,
        getItem: (pos: Int) -> Any?,
        fetchAdvert: () -> Any?,
        createAdItem: (ad: NativeAd) -> AdItem,
        updateItemAd: (pos: Int, adItem: AdItem) -> Unit
    ) {
        val layoutManager = recyclerView.layoutManager
        if (layoutManager !is LinearLayoutManager) return

        val start = layoutManager.findFirstVisibleItemPosition()
        val end = layoutManager.findLastVisibleItemPosition()
        if (start < 0 || end < 0) {
            return
        }
        for (position in start..end) {
            if (position < 0 || position >= itemCount) continue
            val item = getItem(position)
            if (item !is PlaceholderItem) continue
            val newItem: AdItem? = when (val ad = fetchAdvert()) {
                is NativeAd -> createAdItem(ad)
                else -> null
            }
            if (newItem != null) {
                updateItemAd(position, newItem)
            }
        }
    }


    inline fun <reified Placeholder, reified AdvertItem> reloadAdIfNull(
        activity: Activity,
        lifecycleOwner: LifecycleOwner,
        scope: CoroutineScope,
        layoutManager: LinearLayoutManager,
        crossinline getDataSource: () -> List<Any>?,
        crossinline getAdObject: () -> Ad,
        crossinline getAd: () -> Any?,
        crossinline createAdvertItem: (ad: NativeAd) -> AdvertItem,
        crossinline updateItemAd: (pos: Int, adItem: AdvertItem) -> Unit
    ) {
        scope.launch {
            delay(100L)
            if (!lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                return@launch
            }

            var start = layoutManager.findFirstVisibleItemPosition()
            var end = layoutManager.findLastVisibleItemPosition()
            if (start < 0 || end < 0) {
                Log.d("reloadAdIfNull", "first start visible or end visible is -1")
                reportCustomException("reloadAdIfNull first start visible or end visible is -1")
                delay(200L)
                if (!lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                    return@launch
                }
                start = layoutManager.findFirstVisibleItemPosition()
                end = layoutManager.findLastVisibleItemPosition()
                if (start < 0 || end < 0) {
                    Log.d("reloadAdIfNull", "second start visible or end visible is -1")
                    reportCustomException("reloadAdIfNull second start visible or end visible is -1")
                    return@launch
                }
            }
            Log.d("reloadAdIfNull", "start=$start, end=$end")
            val value = getDataSource() ?: return@launch
            val items = value as ArrayList<*>
            val feedAd = getAdObject()
            var adCount = 0
            for (i in start..end) {
                Log.d("reloadAdIfNull","item[$i]=${items[i]}")
                if (items[i] !is Placeholder) {
                    continue
                }

                Log.d("reloadAdIfNull", "i [$i] start [$start] end [$end]")
                when (val ad = getAd()) {
                    is NativeAd -> {
                        updateItemAd.invoke(i, createAdvertItem(ad))
                    }
                    else -> {
                        Log.d("reloadAdIfNull", "i [$i] add listener")
                        feedAd.addAdListener(object : AdListener() {
                            val adIndex = adCount

                            init {
                                val adListener = this
                                lifecycleOwner.lifecycle.addObserver(object :
                                    LifecycleEventObserver {
                                    override fun onStateChanged(
                                        source: LifecycleOwner,
                                        event: Lifecycle.Event
                                    ) {
                                        //Log.d("reloadAdIfNull", "event=$event source=$source")
                                        if(event == Lifecycle.Event.ON_PAUSE){
                                            listenerValid = false
                                        }
                                    }

                                })
                            }

                            override fun onAdLoaded(oid: String) {
                                Log.d("reloadAdIfNull", "onAdLoaded listenerValid $listenerValid")
                                if (!listenerValid) {
                                    return
                                }
                                listenerValid = false
                                scope.launch {
                                    Log.d("reloadAdIfNull", "adCount $adIndex")
                                    delay(adIndex * 20L)
                                    if (!lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                                        return@launch
                                    }
                                    val s = layoutManager.findFirstVisibleItemPosition()
                                    val e = layoutManager.findLastVisibleItemPosition()
                                    Log.d("reloadAdIfNull", "direct after s=$s e=$e")
                                    if (i !in s..e) {
                                        return@launch
                                    }

                                    when (val advert = getAd()) {
                                        is NativeAd -> {
                                            updateItemAd.invoke(i, createAdvertItem(advert))
                                            Log.d("reloadAdIfNull", "getAd [$i] [$advert]")
                                            return@launch
                                        }
                                        else -> {
                                            Log.d("reloadAdIfNull", "getAd null")
                                        }
                                    }
                                }
                            }

                            override fun onAdLoadError(oid: String, errorMsg: String) {
                                listenerValid = false
                                Log.d("reloadAdIfNull", "onAdLoadError oid[$oid] [$errorMsg]")
                            }
                        })
                        val adCached = feedAd.load(activity)
                        Log.d("reloadAdIfNull", "adCached [$adCached]")
                    }
                }
                adCount++
            }
        }
    }

}