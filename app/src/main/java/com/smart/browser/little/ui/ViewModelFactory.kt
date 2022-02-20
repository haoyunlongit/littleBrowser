package com.smart.browser.little.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smart.browser.little.ui.categories.AppsOfCategoryViewModel
import com.smart.browser.little.ui.categories.CategoriesViewModel
import com.smart.browser.little.ui.favorites.FavoritesViewModel
import com.smart.browser.little.ui.news.FeedViewModel
import com.smart.browser.little.ui.games.GamesOfCategoryViewModel
import com.smart.browser.little.ui.games.GamesViewModel
import com.smart.browser.little.ui.home.HomeViewModel
import com.smart.browser.little.ui.search.SearchViewModel
import com.smart.browser.little.ui.splash.SplashViewModel
import com.smart.browser.little.ui.web.WebViewModel
import com.art.maker.data.SitesRepository

/**
 * ViewModel工厂.
 *
 * @author yushaojian
 * @date 2021-05-27 09:01
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val sitesRepository: SitesRepository, private val application: Application
) : ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = with(modelClass) {
        when {
            isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel(sitesRepository, application)
            isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(sitesRepository)
            isAssignableFrom(GamesViewModel::class.java) -> GamesViewModel(sitesRepository)
            isAssignableFrom(GamesOfCategoryViewModel::class.java) -> GamesOfCategoryViewModel(sitesRepository)
            isAssignableFrom(FavoritesViewModel::class.java) -> FavoritesViewModel(sitesRepository)
            isAssignableFrom(WebViewModel::class.java) -> WebViewModel(sitesRepository)
            isAssignableFrom(SearchViewModel::class.java) -> SearchViewModel(sitesRepository)
            isAssignableFrom(CategoriesViewModel::class.java) -> CategoriesViewModel(sitesRepository)
            isAssignableFrom(AppsOfCategoryViewModel::class.java) -> AppsOfCategoryViewModel(sitesRepository)
            isAssignableFrom(FeedViewModel::class.java) -> FeedViewModel(sitesRepository)
            else -> super.create(modelClass)
        } as T
    }
}