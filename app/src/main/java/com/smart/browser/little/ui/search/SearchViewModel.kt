package com.smart.browser.little.ui.search

import androidx.lifecycle.*
import com.art.maker.data.SitesRepository
import com.art.maker.data.model.Site
import kotlinx.coroutines.launch

/**
 * 搜索ViewModel.
 *
 * @author yushaojian
 * @date 2021-05-30 15:34
 */
class SearchViewModel(private val sitesRepository: SitesRepository) : ViewModel() {

    private val _items = MutableLiveData<List<ResultItem>>()
    val items: LiveData<List<ResultItem>> = _items

    val empty: LiveData<Boolean> = _items.map {
        it.isEmpty()
    }

    init {
        search(DEFAULT_QUERY)
    }

    fun search(query: String) {
        viewModelScope.launch {
            val appsByName = sitesRepository.searchApps(query)
            val appsByCategory = sitesRepository.getAppsOfCategory(query)
            val apps = ArrayList<Site>(appsByName.size + appsByCategory.size)
            apps.addAll(appsByName)
            appsByCategory.forEach { if (!appsByName.contains(it)) apps.add(it) }

            val results = ArrayList<ResultItem>()
            results.addAll(apps.map { SiteItem(it) })

            if (results.isEmpty()) {
                results.addAll(suggestionEngines.map { SuggestionItem(it, query) })
            }

            _items.value = results
        }
    }

    private companion object {
        private const val DEFAULT_QUERY = "search"
        private val suggestionEngines = listOf(
            Site("Google", "file:///android_asset/icons/google.jpg", "https://www.google.com/search?q="),
            Site("bing", "file:///android_asset/icons/bing.jpg", "https://www.bing.com/search?q="),
            Site("Yahoo", "file:///android_asset/icons/yahoomail.jpg", "https://www.search.yahoo.com/?q="),
            Site("Yandex", "file:///android_asset/icons/yandex.jpg", "https://www.yandex.com/search?text=")
        )
    }
}