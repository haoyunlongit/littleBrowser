package com.smart.browser.little.ui.splash

import android.app.Activity
import android.app.Application
import android.os.SystemClock
import android.text.format.DateUtils
import android.widget.Toast
import androidx.lifecycle.*
import com.smart.browser.little.Event
import com.smart.browser.little.ad.*
import com.smart.browser.little.config.MAX_SPLASH_DURATION_DEFAULT
import com.smart.browser.little.config.MAX_SPLASH_DURATION_KEY
import com.smart.browser.little.config.MIN_SPLASH_DURATION
import com.smart.browser.little.config.RemoteConfig
import com.art.maker.ad.AdManager
import com.art.maker.data.SitesRepository
import com.sea.proxy.manager.CountryPermissionManager
import kotlinx.coroutines.*

/**
 * 启动页ViewModel.
 *
 * @author yushaojian
 * @date 2021-06-06 06:10
 */
class SplashViewModel(private val sitesRepository: SitesRepository, application: Application) :
    AndroidViewModel(application) {

    private val _showPrivacyPolicy = MutableLiveData<Boolean>()
    val showPrivacyPolicy: LiveData<Boolean> = _showPrivacyPolicy

    private val _privacyPolicyAcceptedEvent = MutableLiveData<Event<Unit>>()
    val privacyPolicyAcceptedEvent: LiveData<Event<Unit>> = _privacyPolicyAcceptedEvent

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    // 数据加载结束（包括超时）
    private val _dataLoadCompletedEvent = MutableLiveData<Event<Unit>>()
    val dataLoadCompletedEvent: LiveData<Event<Unit>> = _dataLoadCompletedEvent

    private var timeoutJob: Job? = null

    private var mDebugCount = 0


    init {
        _showPrivacyPolicy.value = privacyPolicyNotAccepted()
    }

    fun acceptPrivacyPolicy() {
        setPrivacyPolicyAccepted()
        _showPrivacyPolicy.value = false
        _privacyPolicyAcceptedEvent.value = Event(Unit)
    }

    fun start(activity: Activity) {
        if (_showPrivacyPolicy.value == true) {
            return
        }

        if (timeoutJob != null) return

        loadData(activity)
        preloadData()
        timeoutJob = startTimeoutTask()
    }

    fun debugProxySwitch(){
        mDebugCount++
        if(mDebugCount == 5){
            val setSimCode = CountryPermissionManager.instance.getSetSimCode()

            if("in" == setSimCode){
                CountryPermissionManager.instance.setSetSimCode("us")
                CountryPermissionManager.instance.setIgnoreIpBlock(false)
                Toast.makeText(getApplication(), "us is selected", Toast.LENGTH_SHORT).show()
            }else{
                CountryPermissionManager.instance.setSetSimCode("in")
                CountryPermissionManager.instance.setIgnoreIpBlock(true)
                Toast.makeText(getApplication(), "in is selected", Toast.LENGTH_SHORT).show()
            }
            mDebugCount = 0
        }
    }

    // 加载数据，在超时前一直等待
    private fun loadData(activity: Activity) {
        val start = SystemClock.elapsedRealtime()

        _dataLoading.value = true
        MainScope().launch {
            AdManager.setAdConfig(getAdConfig(getApplication()))

            preloadAds(activity)

            val list = listOf(
                async { AppOpenAd.suspendLoad(activity) },
                async { SplashInter.suspendLoad(activity) },
                async { SplashNativeInter.suspendLoad(activity) },
                async { BannerInterAd.load(activity) }
            )
            list.awaitAll()

            if (timeoutJob?.isCompleted == true) {
                return@launch
            }

            val elapsed = SystemClock.elapsedRealtime() - start
            if (elapsed < MIN_SPLASH_DURATION) {
                delay(MIN_SPLASH_DURATION - elapsed)
            }
            _dataLoadCompletedEvent.value = Event(Unit)
            _dataLoading.value = false
        }
    }

    // 预加载数据，无须等待
    private fun preloadData() {
        RemoteConfig.fetchAndActivate()
        MainScope().launch {
            sitesRepository.refreshGames(this)
        }
    }

    // 预加载广告，无须等待
    private fun preloadAds(activity: Activity) {
        HomeFeedAd.load(activity)
        SeeAllRewardAd.load(activity)
    }

    private fun startTimeoutTask(): Job = viewModelScope.launch {
        var maxSplashDuration = RemoteConfig.getInt(MAX_SPLASH_DURATION_KEY) * DateUtils.SECOND_IN_MILLIS
        if (maxSplashDuration <= 0) {
            maxSplashDuration = MAX_SPLASH_DURATION_DEFAULT
        }
        delay(maxSplashDuration)
        if (_dataLoadCompletedEvent.value == null) {
            _dataLoadCompletedEvent.value = Event(Unit)
        }
    }

}