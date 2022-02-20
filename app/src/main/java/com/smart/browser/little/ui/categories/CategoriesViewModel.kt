package com.smart.browser.little.ui.categories

import androidx.lifecycle.*
import com.smart.browser.little.Event
import com.smart.browser.little.ad.SeeAllRewardAd
import com.smart.browser.little.manager.isUnlockedCategory
import com.smart.browser.little.manager.recordUnlockedCategory
import com.smart.browser.little.manager.unlockingCategoryKey
import com.art.maker.ad.AdListener
import com.art.maker.data.SitesRepository
import com.art.maker.data.Result
import com.art.maker.data.model.Category
import kotlinx.coroutines.launch

/**
 * 分类页.
 *
 * @author yushaojian
 * @date 2021-05-31 06:42
 */
class CategoriesViewModel(private val sitesRepository: SitesRepository) : ViewModel() {

    private val _items = MutableLiveData<List<StatefulCategory>>()
    val items: LiveData<List<StatefulCategory>> = _items

    private val _loadSeeAllRewardAdEvent = MutableLiveData<Event<Unit>>()
    val loadSeeAllRewardAdEvent: LiveData<Event<Unit>> = _loadSeeAllRewardAdEvent

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
        SeeAllRewardAd.addAdListener(adListener)
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _items.value = unpack(sitesRepository.getCategories())
        }
    }

    private fun loadSeeAllAdIfHasLockedItem() {
        val items = _items.value ?: return
        val hasLockedItem = items.any { !it.unlocked }
        if (hasLockedItem) {
            _loadSeeAllRewardAdEvent.value = Event(Unit)
        }
    }

    private fun refreshItemsUnlockStatus(categoryKey: String) {
        val items = _items.value ?: return
        for (item in items) {
            if (item.category.key == categoryKey) {
                item.unlocked = true
            }
        }
        _items.value = items
    }

    private fun unpack(it: Result<List<Category>>) = if (it is Result.Success) {
        it.data.map { StatefulCategory(it, isUnlockedCategory(it.key)) }
    } else {
        emptyList()
    }

    override fun onCleared() {
        super.onCleared()
        SeeAllRewardAd.removeAdListener(adListener)
    }

}