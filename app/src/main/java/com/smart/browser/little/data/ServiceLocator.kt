package com.smart.browser.little.data

import android.content.Context
import com.art.maker.data.source.local.SitesLocalDataSource
import com.smart.browser.little.data.remote.SitesRemoteDataSource
import com.art.maker.data.DefaultSitesRepository
import com.art.maker.data.SitesRepository

/**
 * 数据服务提供者.
 *
 * @author yushaojian
 * @date 2021-05-26 09:49
 */
object ServiceLocator {

    private var appRepository: SitesRepository? = null

    fun provideAppsRepository(context: Context): SitesRepository {
        return appRepository ?: synchronized(this) { createAppsRepository(context).also { appRepository = it } }
    }

    private fun createAppsRepository(context: Context): SitesRepository {
        return DefaultSitesRepository(
            SitesRemoteDataSource(context.applicationContext),
            SitesLocalDataSource(context.applicationContext)
        )
    }

}