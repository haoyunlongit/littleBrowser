package com.smart.browser.little.ui.games

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.art.maker.data.SitesRepository
import com.art.maker.data.model.Site
import kotlinx.coroutines.launch

/**
 * 某一分类下的所有游戏的ViewModel.
 *
 * @author yushaojian
 * @date 2021-07-04 16:39
 */
class GamesOfCategoryViewModel(private val sitesRepository: SitesRepository) : ViewModel() {

    private val _items = MutableLiveData<List<Site>>()
    val items: LiveData<List<Site>> = _items

    fun start(categoryName: String) {
        viewModelScope.launch {
            val games = sitesRepository.getGamesOfCategory(categoryName)
            _items.value = games
        }
    }
}