package com.smart.browser.little.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smart.browser.little.Event
import com.smart.browser.little.ad.HomeFeedAd
import com.smart.browser.little.ad.SeeAllRewardAd
import com.smart.browser.little.config.HOME_FEED_AD_MAX_COUNT_KEY
import com.smart.browser.little.config.HOME_FEED_AD_PERIOD_KEY
import com.smart.browser.little.config.HOME_ICON_ROW_COUNT
import com.smart.browser.little.config.RemoteConfig
import com.smart.browser.little.manager.isUnlockedCategory
import com.smart.browser.little.manager.recordUnlockedCategory
import com.smart.browser.little.manager.unlockingCategoryKey
import com.art.maker.ad.AdListener
import com.art.maker.data.Result
import com.art.maker.data.SitesRepository
import com.art.maker.util.Log
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

const val APP_COUNT_PER_SECTION = 8
private const val GAME_COUNT_PER_ROW = 4

class HomeViewModel(private val sitesRepository: SitesRepository) : ViewModel() {

    private val _homeItems = MutableLiveData<ArrayList<HomeItem>>()
    val homeItems: LiveData<ArrayList<HomeItem>> = _homeItems

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _loadSeeAllRewardAdEvent = MutableLiveData<Event<Unit>>()
    val loadSeeAllRewardAdEvent: LiveData<Event<Unit>> = _loadSeeAllRewardAdEvent

    val mainScope = MainScope()

    private val adListener = object : AdListener() {
        override fun onAdShowed(oid: String) {
            super.onAdShowed(oid)
            loadSeeAllAdIfHasLockedItem()
        }

        override fun onRewardEarned(oid: String) {
            super.onRewardEarned(oid)

            val categoryKey = unlockingCategoryKey ?: return
            refreshItemsUnlockStatus(categoryKey)
            recordUnlockedCategory(categoryKey)
        }

        override fun onAdClosed(oid: String) {
            super.onAdClosed(oid)
            unlockingCategoryKey = null
            loadSeeAllAdIfHasLockedItem()
        }
    }

    init {
        loadData()
        SeeAllRewardAd.addAdListener(adListener)
    }

    private fun loadData() {
        _dataLoading.value = true
        viewModelScope.launch {
            val sectionsResult = sitesRepository.getApps()
            if (sectionsResult is Result.Success) {
                val sections = sectionsResult.data
                val items = ArrayList<HomeItem>()

                val period = RemoteConfig.getInt(HOME_FEED_AD_PERIOD_KEY)
                val maxCount = RemoteConfig.getInt(HOME_FEED_AD_MAX_COUNT_KEY)
                val countPerSection = RemoteConfig.getInt(HOME_ICON_ROW_COUNT) * GAME_COUNT_PER_ROW
                val size = sections.size
                var adCount = 0
                for ((index, section) in sections.withIndex()) {
                    val categoryKey = section.key
                    val unlocked = categoryKey != null && isUnlockedCategory(categoryKey)
                    items.add(
                        SitesItem(
                            section.copy(
                                sites = section.sites.take(
                                    countPerSection
                                )
                            ), unlocked
                        )
                    )
                    if (index >= size - 1) break

                    if (adCount >= maxCount || ((index) % period != 0)) continue
                    var ad: Any?
                    if(adCount == 0){
                        ad = HomeFeedAd.getAd()
                    }else{
                        ad = null
                    }
                    //val ad = HomeFeedAd.getAd()
                    Log.d("home_load_ad", "ad [$ad]")
                    when (ad) {
                        is NativeAd -> items.add(AdItem(ad))
                        else -> items.add(PlaceholderItem)
                    }
                    adCount++
                }

                _homeItems.value = items
                loadSeeAllAdIfHasLockedItem()
            } else {
                _homeItems.value = ArrayList()
            }
            _dataLoading.value = false
        }
    }

    private fun refreshItemsUnlockStatus(categoryKey: String) {
        val items = _homeItems.value ?: return
        for (item in items) {
            if (item is SitesItem && item.siteSection.key == categoryKey) {
                item.unlocked = true
            }
        }
        _homeItems.value = items
    }

    private fun loadSeeAllAdIfHasLockedItem() {
        val items = _homeItems.value ?: return
        val hasLockedItem = items.any { it is SitesItem && !it.unlocked }
        if (hasLockedItem) {
            _loadSeeAllRewardAdEvent.value = Event(Unit)
        }
    }

    override fun onCleared() {
        super.onCleared()
        mainScope.cancel()
        SeeAllRewardAd.removeAdListener(adListener)
    }

}