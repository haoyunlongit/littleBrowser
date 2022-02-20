package com.art.maker.data

import androidx.lifecycle.LiveData
import com.art.maker.data.model.Site
import com.art.maker.data.model.SiteSection
import com.art.maker.data.model.Category
import com.art.maker.data.model.News
import kotlinx.coroutines.CoroutineScope

/**
 * 数据层接口.
 *
 * @author yushaojian
 * @date 2021-05-26 09:33
 */
interface SitesRepository {

    fun observeCategories(): LiveData<Result<List<Category>>>

    suspend fun getCategories(): Result<List<Category>>

    suspend fun refreshCategories()

    fun observeApps(): LiveData<Result<List<SiteSection>>>

    suspend fun getApps(): Result<List<SiteSection>>

    suspend fun refreshApps()

    fun observeGames(): LiveData<Result<List<SiteSection>>>

    suspend fun getGames(scope: CoroutineScope): Result<List<SiteSection>>

    suspend fun getGamesOfCategory(categoryName: String): List<Site>

    suspend fun refreshGames(scope: CoroutineScope)

    fun observeFavorites(): LiveData<Result<List<Site>>>

    suspend fun getFavorites(): Result<List<Site>>

    suspend fun refreshFavorites()

    suspend fun saveFavorite(site: Site)

    suspend fun deleteFavorite(site: Site)

    suspend fun searchApps(query: String): List<Site>

    suspend fun getAppsOfCategory(categoryName: String): List<Site>

    suspend fun getNewsList(category: String, offset:Int,limit:Int): Result<News>
}