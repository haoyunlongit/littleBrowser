package com.smart.browser.little.data.remote

import android.annotation.SuppressLint
import android.content.Context
import android.util.SparseArray
import com.smart.browser.little.config.RemoteConfig
import com.smart.browser.little.util.AdvertisingIdClient
import com.smart.browser.little.util.CountryCodeHelper
import com.smart.browser.little.util.LocalLanguageHelper
import com.art.maker.data.Result
import com.art.maker.data.SitesDataSource
import com.art.maker.data.model.Category
import com.art.maker.data.model.News
import com.art.maker.data.model.Site
import com.art.maker.data.model.SiteSection
import com.art.maker.util.isNetworkAvailable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*

private const val PAGE_HOME_KEY = "page_home"

private const val PAGE_CATEGORIES_KEY = "categories"

/**
 * 远程数据源.
 *
 * @author yushaojian
 * @date 2021-06-04 06:59
 */
class SitesRemoteDataSource(private val context: Context) : SitesDataSource {

    private var appsSectionsCache: List<SiteSection>? = null
    private var categoriesCache: List<Category>? = null
    private val gson by lazy { Gson() }

    override suspend fun getCategories(): Result<List<Category>> {
        return withContext(Dispatchers.IO) {
            try {
                Result.Success(getCategoriesFromRemote())
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun getApps(): Result<List<SiteSection>> = withContext(Dispatchers.IO) {
        return@withContext try {
            Result.Success(getAppSections())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getGames(scope: CoroutineScope): Result<List<SiteSection>> {
        if (!isNetworkAvailable(context)) {
            return Result.Error(IllegalStateException("no network connection"))
        }

        return try {
            val size = gameTypes.size()
            val sections = mutableListOf<Deferred<SiteSection?>>()
            for (i in 0 until size) {
                val type = gameTypes.keyAt(i)
                val name = gameTypes.valueAt(i)
                val section = scope.async { fetchGames(type.toString(), name) }
                sections.add(section)
            }

            val list = ArrayList<SiteSection>()
            for (section in sections) {
                section.await()?.let { list.add(it) }
            }
            if (list.isEmpty()) {
                Result.Error(IllegalStateException("no data"))
            } else {
                list.sortByDescending { it.sites.size }
                Result.Success(list)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getGamesOfCategory(categoryName: String): List<Site> {
        return try {
            val apps = ArrayList<Site>()
            val gameResponse = service.fetchGames(categoryName)
            gameResponse.data.list.forEach {
                apps.add(Site(it.name, it.icon, it.gameurl))
            }
            apps
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun fetchGames(type: String, name: String): SiteSection? {
        val games = getGamesOfCategory(type)
        if (games.isEmpty()) return null
        return SiteSection(name, type, games)
    }

    override suspend fun saveGames(sites: List<SiteSection>) {
        throw IllegalAccessException("Not supported by remote source")
    }

    override suspend fun getFavorites(): Result<List<Site>> {
        throw IllegalAccessException("Not supported by remote source")
    }

    override suspend fun saveFavorite(site: Site) {
        throw IllegalAccessException("Not supported by remote source")
    }

    override suspend fun deleteFavorite(site: Site) {
        throw IllegalAccessException("Not supported by remote source")
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
        val userID = AdvertisingIdClient.mGoogleAdId
        val countryCode = CountryCodeHelper.fetchCountryCode()
        val language = LocalLanguageHelper.getLanguage()
        val apiKey = "airm-news-0adf4da0-40a9-11ec-a01f-eba454796127"
        val cat = category
        val result = try {
            newsService.fetchNews(
                offset,
                limit,
                userID,
                countryCode,
                language,
                cat,
                apiKey
            )
        } catch (e: Exception) {
            return Result.Error(e)
        }
        if (result.statusCode > 0) {
            return Result.Exception(result.statusCode, result.statusDescription)
        }
        if (result.report?.articles.isNullOrEmpty()) {
            return Result.Error(RuntimeException("getNewsList empty"))
        }
        return Result.Success(result)
    }

    private fun getAppSections(): List<SiteSection> {
        return appsSectionsCache ?: readAppSections().also { appsSectionsCache = it }
    }

    private fun readAppSections(): List<SiteSection> {
        return try {
            val json = RemoteConfig.getString(PAGE_HOME_KEY)
            gson.fromJson(json, object : TypeToken<List<SiteSection>>() {}.type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun getCategoriesFromRemote(): List<Category> {
        return categoriesCache ?: readCategories().also { categoriesCache = it }
    }

    private fun readCategories(): List<Category> {
        return try {
            val json = RemoteConfig.getString(PAGE_CATEGORIES_KEY)
            gson.fromJson(json, object : TypeToken<List<Category>>() {}.type)
        } catch (e: Exception) {
            emptyList()
        }
    }
}

private val gameTypes = SparseArray<String>().apply {
    put(2, "Puzzle")
    put(3, "Kids")
    put(5, "Boy")
    put(14, "Adventure")
    put(32, "Sports")
    put(41, "Racing")
    put(46, "Girl")
    put(48, "Simulation")
    put(61, "Word")
    put(63, "Educational")
    put(86, "Card")
    put(91, "Action")
    put(94, "Arcade")
    put(231, "Music")
    put(232, "Strategy")
    put(233, "Casino")
    put(234, "Role Playing")
    put(235, "Casual")
    put(236, "Trivia")
    put(237, "Board")
}

private val service: GameService by lazy {
    val okHttpClient = OkHttpClient.Builder().build()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://www.cpsense.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    retrofit.create(GameService::class.java)
}

private interface GameService {
    @GET("public/publics/index/list_game?language=&Stort=2&tage=0&name=&tid=262")
    suspend fun fetchGames(
        @Query("type") type: String = "",
        @Query("start") start: Int = 0,
        @Query("many") many: Int = 100
    ): com.smart.browser.little.ui.games.GameResponse
}

private interface NewsService {
    @GET("/news/v1")
    suspend fun fetchNews(
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 20,
        @Query("userID") userID: String,
        @Query("countryCode") countryCode: String,
        @Query("language") language: String,
        @Query("category") category: String,
        @Query("apiKey") apiKey: String,
    ): News
}

private val newsService: NewsService by lazy {
    val okHttpClientBuilder = OkHttpClient.Builder()
    val sslFactory = createSSLSocketFactory()
    if (sslFactory != null) {
        okHttpClientBuilder.sslSocketFactory(createSSLSocketFactory(), TrustAllManager())
            .hostnameVerifier(TrustAllHostnameVerifier())
    }

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.airfind.com")
        .client(okHttpClientBuilder.build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    retrofit.create(NewsService::class.java)
}

@SuppressLint("TrulyRandom")
private fun createSSLSocketFactory(): SSLSocketFactory? {
    var sSLSocketFactory: SSLSocketFactory? = null
    try {
        val sc = SSLContext.getInstance("TLS")
        sc.init(
            null, arrayOf<TrustManager>(TrustAllManager()),
            SecureRandom()
        )
        sSLSocketFactory = sc.socketFactory
    } catch (e: Exception) {
    }
    return sSLSocketFactory
}

private class TrustAllManager : X509TrustManager {
    @Throws(CertificateException::class)
    override fun checkClientTrusted(x509Certificates: Array<X509Certificate>, s: String) {
    }

    @Throws(CertificateException::class)
    override fun checkServerTrusted(x509Certificates: Array<X509Certificate>, s: String) {
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return emptyArray()
    }
}


private class TrustAllHostnameVerifier : HostnameVerifier {
    override fun verify(hostname: String, session: SSLSession?): Boolean {
        return session != null
    }
}