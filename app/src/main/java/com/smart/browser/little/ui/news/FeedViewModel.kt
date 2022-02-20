package com.smart.browser.little.ui.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smart.browser.little.Event
import com.smart.browser.little.ad.HomeNewsAd
import com.smart.browser.little.config.*
import com.art.maker.data.Result
import com.art.maker.data.SitesRepository
import com.art.maker.data.model.NewsArticle
import com.art.maker.util.Log
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class FeedViewModel(private val sitesRepository: SitesRepository) : ViewModel() {

    private val _newsItems = MutableLiveData<List<FeedItem>>()
    val homeItems: LiveData<List<FeedItem>> = _newsItems
    var lastArticles: MutableList<NewsArticle>? = null
    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    val dataError = MutableLiveData<Boolean>()
    val dataErrorMessage = MutableLiveData<String>()

    private val _loadSeeAllRewardAdEvent = MutableLiveData<Event<Unit>>()
    val loadSeeAllRewardAdEvent: LiveData<Event<Unit>> = _loadSeeAllRewardAdEvent

    val mainScope = MainScope()

    var newsOffset = 0

    var mCategory = DEFAULT_CATEGORY

    @Volatile
    var isFirstLoad = true
    private val LIMIT = 10

    init {
        //refresh()
    }

    fun loadMore() {
        loadData(offset = newsOffset)
    }

    fun refresh() {
        loadData(offset = 0)
    }

    private fun loadData(offset: Int) {

        viewModelScope.launch {
            if (isFirstLoad) {
                _dataLoading.value = true
                dataError.postValue(false)
            }
            val sectionsResult = sitesRepository.getNewsList(mCategory, offset, LIMIT)
            if (sectionsResult !is Result.Success) {
                Log.d("sjx","sectionsResult=$sectionsResult")
                if (sectionsResult is Result.Exception) {
                    val code = sectionsResult.code
                    if (newsOffset > 0 && code == 206) {
                        return@launch
                    }
                    if(newsOffset == 0){
                        _dataLoading.postValue(false)
                        dataError.postValue(true)
                        dataErrorMessage.postValue(sectionsResult.msg?:"")
                    }
                }
                _newsItems.value = ArrayList()
                return@launch
            }
            val sections = sectionsResult.data.report?.articles
            if (newsOffset == 0 && sections.isNullOrEmpty()) {
                _newsItems.value = ArrayList()
                return@launch
            }

            val items = ArrayList<FeedItem>()

            val period = RemoteConfig.getInt(HOME_NEWS_FEED_AD_PERIOD_KEY)
            val maxCount = RemoteConfig.getInt(HOME_NEWS_FEED_AD_MAX_COUNT_KEY)

            val append = mutableListOf<NewsArticle>()
            if (offset <= 0) {
                lastArticles?.clear()
            }
            if (!lastArticles.isNullOrEmpty()) {
                append.addAll(lastArticles!!)
            }
            if (sections != null) {
                append.addAll(sections)
            }
            val size = append.size
            lastArticles = append
            this@FeedViewModel.newsOffset = append.size
            var adCount = 0
            for ((index, section) in append.withIndex()) {
                items.add(
                    NewsItem(
                        section.copy(
                            section.id,
                            section.promoted,
                            section.title,
                            section.description,
                            section.impressionURL,
                            section.clickURL,
                            section.shareURL,
                            section.thumbnailURL,
                            section.largeThumbnailURL,
                            section.xlargeThumbnailURL,
                            section.publishTime,
                            section.author,
                            section.categories,
                        )
                    )
                )
                if (index >= size - 1) break

                if (adCount >= maxCount || ((index) % period != 0)) continue
                //val ad = HomeNewsAd.getAd()
                var ad: Any?
                if(adCount == 0){
                    ad = HomeNewsAd.getAd()
                }else{
                    ad = null
                }
                Log.d("home_news_load_ad", "ad [$ad]")
                when (ad) {
                    is NativeAd -> items.add(AdItem(ad))
                    else -> items.add(PlaceholderItem)
                }
                adCount++
            }

            _newsItems.value = items
            if (isFirstLoad) {
                _dataLoading.value = false
                isFirstLoad = false
            }
        }

    }

    override fun onCleared() {
        super.onCleared()
        mainScope.cancel()
        isFirstLoad = true
        newsOffset = 0
    }

}