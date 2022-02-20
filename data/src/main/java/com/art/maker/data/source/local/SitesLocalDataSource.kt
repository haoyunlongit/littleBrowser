package com.art.maker.data.source.local

import android.content.Context
import com.smart.browser.little.data.R
import com.art.maker.data.SitesDataSource
import com.art.maker.data.Result
import com.art.maker.data.model.Site
import com.art.maker.data.model.SiteSection
import com.art.maker.data.model.Category
import com.art.maker.data.model.News
import com.art.maker.util.readTextFileFromResource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.RuntimeException

/**
 * apps本地数据源.
 *
 * @author yushaojian
 * @date 2021-05-26 09:32
 */
class SitesLocalDataSource(private val context: Context) : SitesDataSource {

    private var appsSectionsCache: List<SiteSection>? = null
    private val gson by lazy { Gson() }

    override suspend fun getCategories(): Result<List<Category>> = withContext(Dispatchers.IO) {
        val config = readTextFileFromResource(context, R.raw.categories)
        try {
            val categories: List<Category> =
                gson.fromJson(config, object : TypeToken<List<Category>>() {}.type)
            Result.Success(categories)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getApps(): Result<List<SiteSection>> = withContext(Dispatchers.IO) {
        return@withContext try {
            Result.Success(getAppSections())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getGames(scope: CoroutineScope): Result<List<SiteSection>> =
        withContext(Dispatchers.IO) {
            return@withContext readGameSections()
        }

    override suspend fun getGamesOfCategory(categoryName: String): List<Site> =
        withContext(Dispatchers.IO) {
            val gameSections = readGameSections()
            if (gameSections is Result.Success) {
                for (section in gameSections.data) {
                    if (section.key == categoryName) return@withContext section.sites
                }
            }
            return@withContext emptyList()
        }

    override suspend fun saveGames(sites: List<SiteSection>) {
        try {
            val file = File(context.filesDir, GAMES_CACHE_FILE)
            val json = gson.toJson(sites)
            file.writeText(json)
        } catch (e: Exception) {
        }
    }

    override suspend fun getFavorites(): Result<List<Site>> {
        return Result.Success(readFavorites())
    }

    override suspend fun saveFavorite(site: Site) {
        val favorites = mutableListOf<Site>()
        favorites.addAll(readFavorites())
        favorites.add(site)
        writeFavorites(favorites)
    }

    override suspend fun deleteFavorite(site: Site) {
        val favorites = mutableListOf<Site>()
        favorites.addAll(readFavorites())
        favorites.remove(site)
        writeFavorites(favorites)
    }

    private suspend fun readFavorites(): List<Site> = withContext(Dispatchers.IO) {
        try {
            val file = File(context.filesDir, FAVORITES_FILE)
            val text = file.readText()
            val sites: List<Site> = gson.fromJson(text, object : TypeToken<List<Site>>() {}.type)
            sites
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun writeFavorites(favorites: List<Site>) = withContext(Dispatchers.IO) {
        try {
            val text = gson.toJson(favorites)
            val file = File(context.filesDir, FAVORITES_FILE)
            file.writeText(text)
        } catch (e: Exception) {
        }
    }

    override suspend fun searchApps(query: String): List<Site> {
        val apps = mutableListOf<Site>()
        for (section in getAppSections()) {
            val sites = section.sites.filter { it.name.contains(query, true) }
            sites.forEach {
                if (!apps.contains(it)) apps.add(it)
            }
        }
        return apps
    }

    override suspend fun getAppsOfCategory(categoryName: String): List<Site> {
        val apps = mutableListOf<Site>()
        for (section in getAppSections()) {
            if (section.key?.contains(categoryName, true) == true) {
                section.sites.forEach {
                    if (!apps.contains(it)) apps.add(it)
                }
            }
        }
        return apps
    }

    override suspend fun getNewsList(category: String, offset: Int, limit: Int): Result<News> {
        return Result.Error(RuntimeException("getNewsList wrong "))
    }

    private fun getAppSections(): List<SiteSection> {
        return appsSectionsCache ?: readAppSections().also { appsSectionsCache = it }
    }

    private fun readAppSections(): List<SiteSection> {
        return try {
            val json = readTextFileFromResource(context, R.raw.page_home)
            gson.fromJson(json, object : TypeToken<List<SiteSection>>() {}.type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun readGameSections(): Result<List<SiteSection>> {
        return try {
            val file = File(context.filesDir, GAMES_CACHE_FILE)
            var json = if (file.exists()) file.readText() else ""
            if (json.isBlank()) {
                json = readTextFileFromResource(context, R.raw.page_game)
            }
            if (json.isBlank()) {
                Result.Error(IllegalArgumentException("no local games"))
            } else {
                val siteSections: List<SiteSection> =
                    gson.fromJson(json, object : TypeToken<List<SiteSection>>() {}.type)
                Result.Success(siteSections)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private companion object {
        private const val FAVORITES_FILE = "favorites_apps"
        private const val GAMES_CACHE_FILE = "games_cache"
    }

}