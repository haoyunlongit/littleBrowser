package com.smart.browser.little.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.art.maker.data.SitesRepository
import com.art.maker.data.model.Site
import kotlinx.coroutines.launch

/**
 * 某一分类下的所有Apps的ViewModel.
 *
 * @author yushaojian
 * @date 2021-05-31 09:17
 */
class AppsOfCategoryViewModel(private val sitesRepository: SitesRepository) : ViewModel() {

    private val _items = MutableLiveData<List<Site>>()
    val items: LiveData<List<Site>> = _items

    fun start(categoryName: String) {
        viewModelScope.launch {
            val apps = sitesRepository.getAppsOfCategory(categoryName)
            _items.value = apps.filterNot { it.name.contains("Youtube") }
        }
    }
}