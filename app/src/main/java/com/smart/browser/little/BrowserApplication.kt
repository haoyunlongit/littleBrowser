package com.smart.browser.little

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.smart.browser.little.ad.Ad
import com.smart.browser.little.util.AdvertisingIdClient
import com.smart.browser.little.config.RemoteConfig
import com.smart.browser.little.config.defaultRemoteConfigs
import com.smart.browser.little.market.MarketEventHelper
import com.smart.browser.little.report.reportEvent
import com.smart.browser.little.ui.feed.initTaboola
import com.art.maker.util.SPUtils
import com.sea.proxy.ProxyInit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.flurry.android.FlurryAgent

/**
 * 浏览器Application.
 *
 * @author yushaojian
 * @date 2021-06-06 14:49
 */
class BrowserApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
        SPUtils.init(this, "browser_pref")
        val processName = com.art.maker.util.getProcessName(this)
        initProxy(processName)
        if (processName == packageName) {
            initOnMainProcess()
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    private fun initOnMainProcess() {
        MarketEventHelper.recordAppInstalledTime()
        RemoteConfig.setDefaultsAsync(defaultRemoteConfigs)
        AdvertisingIdClient.initAdId(this)
        Ad.init(this)
        initTaboola()
        initFlurry()
    }

    private fun initProxy(processName: String?) {
        ProxyInit.INSTANCE.init(context, BuildConfig.DEBUG, object : ProxyInit.ProxyCallBack {
            override fun sendEvent(action: String, type: String?, page: String?, extra: String?) {
                reportEvent(action, type, page, extra)
            }

            override fun onProxyAuthorization() {
                MarketEventHelper.onProxyAuthorization()
            }
        })
        if (processName == packageName) {
            ProxyInit.INSTANCE.updateSwitchConfig(RemoteConfig.getString("proxy_switch"))
            GlobalScope.launch(Dispatchers.IO) {
                RemoteConfig.fetchAndWaite()
                ProxyInit.INSTANCE.updateSwitchConfig(RemoteConfig.getString("proxy_switch"))
                ProxyInit.INSTANCE.updateNodeConfig(RemoteConfig.getString("ss_android_config"))
            }
        }
    }

    private fun initFlurry() {
        FlurryAgent.Builder()
            .withLogEnabled(false)
            .build(this, "QC8F2QKTD7VMR98RH9W3")
    }
}