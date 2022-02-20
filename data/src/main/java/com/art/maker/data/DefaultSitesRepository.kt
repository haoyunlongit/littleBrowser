package com.art.maker.data

import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.art.maker.data.model.Site
import com.art.maker.data.model.SiteSection
import com.art.maker.data.model.Category
import com.art.maker.data.model.News
import com.art.maker.util.SPUtils
import kotlinx.coroutines.CoroutineScope

/**
 * 数据层接口[SitesRepository]默认实现. 访问apps数据的单一入口.
 *
 * @author yushaojian
 * @date 2021-05-26 09:34
 */
class DefaultSitesRepository(
    private val sitesRemoteDataSource: SitesDataSource,
    private val sitesLocalDataSource: SitesDataSource
) : SitesRepository {

    private val observableCategories = MutableLiveData<Result<List<Category>>>()
    private val observableApps = MutableLiveData<Result<List<SiteSection>>>()
    private val observableGames = MutableLiveData<Result<List<SiteSection>>>()
    private val observableFavorites = MutableLiveData<Result<List<Site>>>()

    override fun observeCategories(): LiveData<Result<List<Category>>> {
        return observableCategories
    }

    override suspend fun getCategories(): Result<List<Category>> {
        val categories = sitesRemoteDataSource.getCategories()
        if (categories is Result.Success && categories.data.isNotEmpty()) {
            return categories
        }
        return sitesLocalDataSource.getCategories()
    }

    override suspend fun refreshCategories() {
        val categories = getCategories()
        observableCategories.value = categories
    }

    override fun observeApps(): LiveData<Result<List<SiteSection>>> {
        return observableApps
    }

    override suspend fun getApps(): Result<List<SiteSection>> {
        val remoteApps = sitesRemoteDataSource.getApps()
        if (remoteApps is Result.Success && remoteApps.data.isNotEmpty()) {
            return remoteApps
        }
        return sitesLocalDataSource.getApps()
    }

    override suspend fun refreshApps() {
        val apps = getApps()
        observableApps.value = apps
    }

    override fun observeGames(): LiveData<Result<List<SiteSection>>> {
        return observableGames
    }

    override suspend fun getGames(scope: CoroutineScope): Result<List<SiteSection>> {
        val memCache = observableGames.value
        if (memCache is Result.Success) {
            return memCache
        }

//        val now = System.currentTimeMillis()
//        val cacheTime = SPUtils.getLong(GAMES_CACHE_TIME, 0)
//
//        if (now - cacheTime < GAMES_CACHE_DURATION) {
//            val diskCache = sitesLocalDataSource.getGames(scope)
//            if (diskCache is Result.Success && diskCache.data.isNotEmpty()) {
//                return diskCache
//            }
//        }

        val onlineGames = sitesRemoteDataSource.getGames(scope)
        if (onlineGames is Result.Success) {
            sitesLocalDataSource.saveGames(onlineGames.data)
//            SPUtils.putLong(GAMES_CACHE_TIME, now)
        } else {
            val diskCache = sitesLocalDataSource.getGames(scope)
            if (diskCache is Result.Success && diskCache.data.isNotEmpty()) {
                return diskCache
            }
        }

        return onlineGames
    }

    override suspend fun getGamesOfCategory(categoryName: String): List<Site> {
        val diskCache = sitesLocalDataSource.getGamesOfCategory(categoryName)
        if (diskCache.isNotEmpty()) return diskCache
        return sitesRemoteDataSource.getGamesOfCategory(categoryName)
    }

    override suspend fun refreshGames(scope: CoroutineScope) {
        val games = getGames(scope)
        observableGames.value = games
    }

    override fun observeFavorites(): LiveData<Result<List<Site>>> {
        return observableFavorites
    }

    override suspend fun getFavorites(): Result<List<Site>> {
        return sitesLocalDataSource.getFavorites()
    }

    override suspend fun refreshFavorites() {
        val favorites = getFavorites()
        observableFavorites.value = favorites
    }

    override suspend fun saveFavorite(site: Site) {
        sitesLocalDataSource.saveFavorite(site)
        refreshFavorites()
    }

    override suspend fun deleteFavorite(site: Site) {
        sitesLocalDataSource.deleteFavorite(site)
        refreshFavorites()
    }

    override suspend fun searchApps(query: String): List<Site> {
        val remoteApps = sitesRemoteDataSource.searchApps(query)
        if (!remoteApps.isNullOrEmpty()) {
            return remoteApps
        }
        return sitesLocalDataSource.searchApps(query)
    }

    override suspend fun getAppsOfCategory(categoryName: String): List<Site> {
        val remoteApps = sitesRemoteDataSource.getAppsOfCategory(categoryName)
        if (!remoteApps.isNullOrEmpty()) {
            return remoteApps
        }
        return sitesLocalDataSource.getAppsOfCategory(categoryName)
    }

    override suspend fun getNewsList(category: String, offset: Int, limit: Int): Result<News> {
        return sitesRemoteDataSource.getNewsList(category, offset, limit)
    }

    private companion object {
        private const val GAMES_CACHE_TIME = "games_cache_time"          // 游戏列表缓存时间
        private const val GAMES_CACHE_DURATION = DateUtils.DAY_IN_MILLIS // 游戏缓存有效时长
    }
}