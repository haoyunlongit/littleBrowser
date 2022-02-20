package com.art.maker.data

import com.art.maker.data.model.Site
import com.art.maker.data.model.SiteSection
import com.art.maker.data.model.Category
import com.art.maker.data.model.News
import kotlinx.coroutines.CoroutineScope
import org.intellij.lang.annotations.Language

/**
 * apps数据源.
 *
 * @author yushaojian
 * @date 2021-05-26 09:32
 */
interface SitesDataSource {

    suspend fun getCategories(): Result<List<Category>>

    suspend fun getApps(): Result<List<SiteSection>>

    suspend fun getGames(scope: CoroutineScope): Result<List<SiteSection>>

    suspend fun getGamesOfCategory(categoryName: String): List<Site>

    suspend fun saveGames(sites: List<SiteSection>)

    suspend fun getFavorites(): Result<List<Site>>

    suspend fun saveFavorite(site: Site)

    suspend fun deleteFavorite(site: Site)

    suspend fun searchApps(query: String): List<Site>

    suspend fun getAppsOfCategory(categoryName: String): List<Site>

    suspend fun getNewsList(
        category: String,
        offset: Int,
        limit: Int
    ): Result<News>

}