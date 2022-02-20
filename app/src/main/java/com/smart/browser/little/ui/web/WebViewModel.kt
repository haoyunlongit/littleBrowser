package com.smart.browser.little.ui.web

import androidx.lifecycle.*
import com.smart.browser.little.Event
import com.art.maker.data.SitesRepository
import com.art.maker.data.Result
import com.art.maker.data.model.Site
import kotlinx.coroutines.launch

/**
 * 网页ViewModel.
 *
 * @author yushaojian
 * @date 2021-05-28 07:30
 */
class WebViewModel(private val sitesRepository: SitesRepository) : ViewModel() {

    private val _forceUpdate = MutableLiveData<Boolean>()

    private val _favorites: LiveData<List<Site>> = _forceUpdate.switchMap { forceUpdate ->
        if (forceUpdate) {
            viewModelScope.launch { sitesRepository.refreshFavorites() }
        }
        sitesRepository.observeFavorites().distinctUntilChanged().switchMap {
            val result = MutableLiveData<List<Site>>()
            result.value = if (it is Result.Success) it.data else emptyList()
            result
        }
    }

    private val _app = MutableLiveData<Site>()

    private val _isFavorite: LiveData<Boolean> = _app.switchMap { app ->
        _favorites.switchMap { favorites ->
            val result = MutableLiveData<Boolean>()
            result.value = favorites.contains(app)
            result
        }
    }
    val isFavorite: LiveData<Boolean> = _isFavorite
    private val _favoriteToggled = MutableLiveData<Event<Boolean>>()
    val favoriteToggled: LiveData<Event<Boolean>> = _favoriteToggled

    var hasUploadDownloadBtn: Boolean = false

    fun setApp(site: Site) {
        _app.value = site
    }

    fun toggleFavorite(site: Site) {
        val favored = _isFavorite.value == true
        if (favored) {
            viewModelScope.launch { sitesRepository.deleteFavorite(site) }
        } else {
            viewModelScope.launch { sitesRepository.saveFavorite(site) }
        }
        _favoriteToggled.value = Event(!favored)
    }

    init {
        _forceUpdate.value = true
    }

}