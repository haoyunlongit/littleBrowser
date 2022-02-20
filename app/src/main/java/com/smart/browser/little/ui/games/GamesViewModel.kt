package com.smart.browser.little.ui.games

import androidx.lifecycle.*
import com.smart.browser.little.ad.HomeGamesAd
import com.smart.browser.little.config.HOME_GAME_AD_MAX_COUNT_KEY
import com.smart.browser.little.config.HOME_GAME_AD_PERIOD_KEY
import com.smart.browser.little.config.HOME_ICON_ROW_COUNT
import com.smart.browser.little.config.RemoteConfig
import com.art.maker.data.Result
import com.art.maker.data.SitesRepository
import com.art.maker.data.model.SiteSection
import com.art.maker.util.Log
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

private const val GAME_COUNT_PER_SECTION = 6
private const val GAME_COUNT_PER_ROW = 4

class GamesViewModel(private val sitesRepository: SitesRepository) : ViewModel() {

    private val _forceUpdate = MutableLiveData(false)
    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading
    val mainScope = MainScope()

    private val _items: LiveData<ArrayList<GameItem>> = _forceUpdate.switchMap { forceUpdate ->
        if (forceUpdate) {
            _dataLoading.value = true
            viewModelScope.launch {
                sitesRepository.refreshGames(this)
                _dataLoading.value = false
            }
        }
        sitesRepository.observeGames().distinctUntilChanged().switchMap { unpack(it) }
    }
    val items: LiveData<ArrayList<GameItem>> = _items

    private fun unpack(it: Result<List<SiteSection>>): MutableLiveData<ArrayList<GameItem>> {
        val result = MutableLiveData<ArrayList<GameItem>>()
        if (it is Result.Success) {
            val data = it.data
//            result.value = data.map {
//                it.copy(
//                    key = if (it.sites.size > GAME_COUNT_PER_SECTION) it.key else null,
//                    sites = it.sites.take(GAME_COUNT_PER_SECTION)
//                )
//            }
            val items = ArrayList<GameItem>()

            val period = RemoteConfig.getInt(HOME_GAME_AD_PERIOD_KEY)
            val maxCount = RemoteConfig.getInt(HOME_GAME_AD_MAX_COUNT_KEY)
            val countPerSection = RemoteConfig.getInt(HOME_ICON_ROW_COUNT) * GAME_COUNT_PER_ROW
            val size = data.size
            var adCount = 0
            for ((index, section) in data.withIndex()) {
                val categoryKey = section.key
                val unlocked = false/*categoryKey != null && isUnlockedCategory(categoryKey)*/
                items.add(
                    SitesItem(
                        section.copy(
                            key = if (section.sites.size > countPerSection) categoryKey else null,
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
                    ad = HomeGamesAd.getAd()
                }else{
                    ad = null
                }
                Log.d("game_load_ad", "ad [$ad]")
                when (ad) {
                    is NativeAd -> items.add(AdItem(ad))
                    else -> items.add(PlaceholderItem)
                }
                adCount++
            }
            result.value = items
        } else {
            result.value = ArrayList()
        }
        return result
    }

    init {
        _forceUpdate.value = true
    }

}