package com.smart.browser.little.ui.favorites

import androidx.lifecycle.*
import com.art.maker.data.SitesRepository
import com.art.maker.data.Result
import com.art.maker.data.model.Site
import kotlinx.coroutines.launch

class FavoritesViewModel(private val sitesRepository: SitesRepository) : ViewModel() {

    private val _forceUpdate = MutableLiveData(false)

    private val _items: LiveData<List<Site>> = _forceUpdate.switchMap { forceUpdate ->
        if (forceUpdate) {
            viewModelScope.launch { sitesRepository.refreshFavorites() }
        }
        sitesRepository.observeFavorites().distinctUntilChanged().switchMap { unpack(it) }
    }
    val items: LiveData<List<Site>> = _items

    val empty: LiveData<Boolean> = _items.map {
        it.isEmpty()
    }

    private fun unpack(it: Result<List<Site>>): MutableLiveData<List<Site>> {
        val result = MutableLiveData<List<Site>>()
        if (it is Result.Success) {
            val data = it.data
            result.value = data
        } else {
            result.value = emptyList()
        }
        return result
    }

    init {
        _forceUpdate.value = true
    }
}