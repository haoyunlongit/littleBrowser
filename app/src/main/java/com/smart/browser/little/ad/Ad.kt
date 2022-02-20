@file:Suppress("unused")

package com.smart.browser.little.ad

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import com.smart.browser.little.BuildConfig
import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.annotation.CallSuper
import androidx.lifecycle.*
import com.smart.browser.little.BuildConfig.DEBUG
import com.smart.browser.little.MainActivity
import com.smart.browser.little.config.*
import com.smart.browser.little.databinding.AdNativeWithMediaViewBinding
import com.smart.browser.little.report.reportAdImpressionScene
import com.smart.browser.little.market.MarketEventHelper
import com.smart.browser.little.util.DensityUtil
import com.art.maker.ad.*
import com.art.maker.util.Log
import com.art.maker.util.isNetworkAvailable
import com.art.maker.util.isPackageInstalled
import com.art.maker.util.md5
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*

const val OID_APP_OPEN = "app_open" // 开屏广告
const val OID_SPLASH_INTER = "splash_inter" // 插屏开屏广告
const val OID_SPLASH_NATIVE_INTER = "splash_native_inter" // 原生插屏开屏广告
private const val OID_SITE_ENTER = "site_enter" // 进入网站
private const val OID_SITE_EXIT = "site_exit" // 退出网站
private const val OID_EXIT_APP = "exit_app" // 退出app
private const val OID_HOME_FEED = "home_feed" // 主页信息流
private const val OID_HOME_GAMES = "home_games" // 主页游戏
private const val OID_HOME_NEWS = "home_news" // 主页新闻
private const val OID_BANNER_INTER = "banner_inter" // banner插屏
private const val OID_SEE_ALL_REWARD = "see_all_reward" // see all插屏
private const val OID_FAVORITE_NATIVE = "favorite_native" // 收藏页原生

private const val OID_WEB_BOTTOM_BANNER = "web_bottom_banner" // web底部banner
private const val OID_VIDEO_DOWNLOAD_REWARD = "video_download_reward" // see all插屏



/**
 * 定义广告场景.
 *
 * 配置参考文档
 * https://docs.google.com/document/d/1RnRgNNOQxahTHs7xuWXeMctnrZ36XYOyVybqqrptPeE/edit?usp=sharing
 * @author yushaojian
 * @date 2021-06-14 07:04
 */
sealed class Ad(val oid: String) {

    fun removeAdListener(adListener: AdListener) = AdManager.removeAdListener(oid, adListener)

    fun addAdListener(adListener: AdListener) = AdManager.addAdListener(oid, adListener)

    fun removeAdListeners() = AdManager.removeAdListeners(oid)

    fun hasOid() = AdManager.hasOid(oid)

    fun hasCache() = AdManager.hasCache(oid)

    @CallSuper
    open fun load(activity: Activity) = AdManager.load(activity, oid)

    fun getAd(): Any? = AdManager.getAd(oid)

    open suspend fun suspendLoad(activity: Activity) = AdManager.suspendLoad(activity, oid)

    open suspend fun suspendCancellableLoad(activity: Activity) = AdManager.suspendCancellableLoad(activity, oid)

    fun blockShow() = AdManager.blockShow(oid)

    fun cancelBlockShow() = AdManager.cancelBlockShow(oid)

    companion object {

        // 如果安装了这个应用，则自动添加test device，作为测试用户请求广告
        private const val PACKAGE_NAME = "com.willme.topactivity"

        // --------------------------------------
        //              初始化
        // --------------------------------------

        /**
         * 广告初始化.
         *
         * 1. 添加AdMob test device设置
         * 2. 广告模块debug设置
         */
        fun init(application: Application) {
            // 广告测试模式
            setTestDeviceIds(application)
            AdManager.debugAd(BuildConfig.DEV)

            // 初始化AdMob和Facebook
            MobileAds.initialize(application)
            AudienceNetworkAds.initialize(application)

            // 初始化app open广告
            AppOpenAd.init(application)

            AdManager.adReporter = object : AdReporter {
                /**
                 * @param oid: 广告位，如[OID_APP_OPEN]
                 * @param format 广告格式，如[AdFormat.INTERSTITIAL]
                 */
                override fun onAdImpression(oid: String, @AdFormat format: Int) {
                    if (DEBUG) Log.d("onAdImpression oid: $oid format: $format")
                    MarketEventHelper.onAdShow(oid, format)
                    MarketEventHelper.onSplashAdShow(oid, format)
                    reportAdImpressionScene(oid)
                }
            }
        }

        @SuppressLint("HardwareIds")
        private fun setTestDeviceIds(context: Context) {
            val testDevices = try {
                if (!isPackageInstalled(PACKAGE_NAME, context.packageManager)) return
                val aid = Settings.Secure.getString(context.contentResolver, "android_id") ?: return
                val testDevice = aid.md5().uppercase(Locale.ENGLISH)
                listOf(testDevice)
            } catch (e: Exception) {
                return
            }

            val configuration =
                RequestConfiguration.Builder().setTestDeviceIds(testDevices).build()
            MobileAds.setRequestConfiguration(configuration)
        }
    }
}

// -------------------------开屏广告--------------------------------

@SuppressLint("StaticFieldLeak")
object AppOpenAd : Ad(OID_APP_OPEN), LifecycleObserver, Application.ActivityLifecycleCallbacks {

    private var lastShowTime = 0L
    private val minShowInterval = RemoteConfig.getInt(APP_OPEN_AD_INTERVAL_KEY) * DateUtils.SECOND_IN_MILLIS

    private var currentActivity: Activity? = null
    private val adListener = object : AdListener() {
        override fun onAdShowed(oid: String) {
            blockShow() // 避免展示多个开屏广告
        }

        override fun onAdClosed(oid: String) {
            lastShowTime = SystemClock.elapsedRealtime()
            cancelBlockShow()
        }
    }

    fun init(application: Application) {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
        if (activity is MainActivity) {
            load(activity)
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        val activity = currentActivity
        if (activity is MainActivity) {
            show(activity)
        }
        MarketEventHelper.onAppStart()
        MarketEventHelper.onSecondDayRetention()
    }

    fun show(activity: ComponentActivity): Boolean {
        if (minShowInterval > 0 && SystemClock.elapsedRealtime() - lastShowTime < minShowInterval) {
            Log.w("AdLib", "$oid show too frequently, skip showing")
            return false
        }

        if (!isNetworkAvailable(activity.applicationContext)) {
            return false
        }

        removeAdListener(adListener)
        addAdListener(adListener)

        return AdManager.show(activity, oid)
    }
}

// 原生广告
sealed class NativeAd(oid: String) : Ad(oid) {

    protected open fun registerNativeCustom() {}

    fun unregisterNativeCustom() = AdManager.unregisterNativeCustom(oid)

    fun show(lifecycle: Lifecycle, viewGroup: ViewGroup) {
        if (viewGroup.childCount > 0) {
            return // 已经添加了广告，不刷新
        }
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            return
        }

        registerNativeCustom()

        viewGroup.visibility = View.VISIBLE
        val destroyable = AdManager.show(viewGroup, oid)
        scheduleAdDestroy(destroyable, lifecycle)
    }

    /**
     * 安排广告销毁程序.
     */
    private fun scheduleAdDestroy(destroyable: Destroyable?, lifecycle: Lifecycle) {
        if (destroyable == null) return
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                destroyable.destroy()
            }
        })
    }

    @CallSuper
    override fun load(activity: Activity): Boolean {
        registerNativeCustom()
        return super.load(activity)
    }

    override suspend fun suspendLoad(activity: Activity): Boolean {
        registerNativeCustom()
        return super.suspendLoad(activity)
    }
}

// 有MediaView的原生广告
sealed class WithMediaViewNativeAd(oid: String) : NativeAd(oid) {

    override fun registerNativeCustom() {
        if (AdManager.hasNativeCustom(oid)) {
            return // 已经注册了
        }

        AdManager.registerNativeCustom(oid, WithMediaViewNativeCustom)
    }
}

private object WithMediaViewNativeCustom : NativeCustom {

    override fun showNative(viewGroup: ViewGroup, nativeAd: com.google.android.gms.ads.nativead.NativeAd): Boolean {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val adNativeBinding = AdNativeWithMediaViewBinding.inflate(layoutInflater, viewGroup, false)
        populateNativeAdView(nativeAd, adNativeBinding)
        viewGroup.removeAllViews()
        viewGroup.addView(adNativeBinding.root)
        return true
    }

    private fun populateNativeAdView(nativeAd: com.google.android.gms.ads.nativead.NativeAd, adNativeBinding: AdNativeWithMediaViewBinding) {
        // Set the media view.
        val adView = adNativeBinding.root
        adView.mediaView = adNativeBinding.adMedia

        // Set other ad assets.
        adView.headlineView = adNativeBinding.adHeadline
        adView.bodyView = adNativeBinding.adBody
        adView.callToActionView = adNativeBinding.adCta
        adView.iconView = adNativeBinding.adIcon

        // The headline and media content are guaranteed to be in every UnifiedNativeAd.
        adNativeBinding.adHeadline.text = nativeAd.headline
        adNativeBinding.adMedia.setMediaContent(nativeAd.mediaContent)

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adNativeBinding.adBody.visibility = View.GONE
        } else {
            adNativeBinding.adBody.visibility = View.VISIBLE
            adNativeBinding.adBody.text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adNativeBinding.adCta.visibility = View.GONE
        } else {
            adNativeBinding.adCta.visibility = View.VISIBLE
            adNativeBinding.adCta.text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adNativeBinding.adIcon.visibility = View.GONE
        } else {
            adNativeBinding.adIcon.setImageDrawable(nativeAd.icon.drawable)
            adNativeBinding.adIcon.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)
    }
}

object ExitAppAd : NativeAd(OID_EXIT_APP)

object HomeFeedAd : NativeAd(OID_HOME_FEED)

object HomeGamesAd : NativeAd(OID_HOME_GAMES)

object HomeNewsAd : NativeAd(OID_HOME_NEWS)

object FavoriteNativeAd : WithMediaViewNativeAd(OID_FAVORITE_NATIVE)

// 全屏广告（插屏、激励视频、激励插屏）
sealed class FullscreenAd(oid: String) : Ad(oid) {

    private var onClose: (() -> Unit)? = null

    private val adListener = object : AdListener() {
        override fun onAdClosed(oid: String) {
            onClose?.invoke()
        }
    }

    open fun show(activity: ComponentActivity, onClose: (() -> Unit)? = null): Boolean {
        if (!activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            return false
        }
        if (!isNetworkAvailable(activity.applicationContext)) {
            return false
        }

        AdManager.removeAdListener(oid, adListener)
        AdManager.addAdListener(oid, adListener)
        this.onClose = onClose

        return AdManager.show(activity, oid)
    }
}

object SplashInter : FullscreenAd(OID_SPLASH_INTER)

object SplashNativeInter : FullscreenAd(OID_SPLASH_NATIVE_INTER)

// 网站进入广告
object SiteEnterAd : FullscreenAd(OID_SITE_ENTER) {

    private const val UNSET = -1

    private var period = UNSET
    private var offset = UNSET
    private var enterCount = 0

    override fun show(activity: ComponentActivity, onClose: (() -> Unit)?): Boolean {
        if (offset == UNSET) {
            offset = RemoteConfig.getInt(SITE_ENTER_INTER_OFFSET_KEY)
        }
        if (period == UNSET) {
            period = RemoteConfig.getInt(SITE_ENTER_INTER_PERIOD_KEY)
        }
        if (period < 1) return false

        val count = enterCount - offset
        var showed = false
        if (count >= 0 && count % period == 0) {
            showed = super.show(activity, onClose)
            if (!showed) return false // 该展示而没展示，则不计数，以在下次展示
        }
        enterCount++
        return showed
    }
}

// 网站退出广告
object SiteExitAd : FullscreenAd(OID_SITE_EXIT) {

    private var lastShowTime = 0L
    private val minShowInterval = RemoteConfig.getInt(SITE_EXIT_AD_INTERVAL_KEY) * DateUtils.SECOND_IN_MILLIS

    override fun show(activity: ComponentActivity, onClose: (() -> Unit)?): Boolean {
        if (minShowInterval > 0 && SystemClock.elapsedRealtime() - lastShowTime < minShowInterval) {
            Log.w("AdLib", "$oid show too frequently, skip showing")
            return false
        }

        val show = super.show(activity, onClose)
        if (show) {
            lastShowTime = SystemClock.elapsedRealtime()
        }
        return show
    }
}

object BannerInterAd : FullscreenAd(OID_BANNER_INTER)

object SeeAllRewardAd : FullscreenAd(OID_SEE_ALL_REWARD)
object VideoDownloadRewardAd : FullscreenAd(OID_VIDEO_DOWNLOAD_REWARD)

// banner广告
sealed class BannerAd(oid: String) : Ad(oid) {
}

object DirectWebBannerWithAnimatorAd: BannerAd(OID_WEB_BOTTOM_BANNER){

    var loadAdJob: Job?= null
    var destroyable: Destroyable?= null

    override fun load(activity: Activity): Boolean {
        return super.load(activity)
    }

    override suspend fun suspendLoad(activity: Activity): Boolean {
        return super.suspendLoad(activity)
    }

    fun onPageDestroy(){
        Log.d("AdLib","DirectWebBannerWithAnimatorAd onPageDestroy")
        destroyable?.destroy()
        destroyable = null
        loadAdJob?.cancel()
        loadAdJob = null
    }

    fun showAd(activity: ComponentActivity, lifecycle: Lifecycle, viewGroup: ViewGroup){
        scheduleAdDestroy(lifecycle)
        if(hasCache()){
            show(viewGroup)
            return
        }
        loadAdJob = MainScope().launch {
            suspendCancellableLoad(activity)
            if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
                return@launch
            }
            viewGroup.translationX = DensityUtil.getScreenWidth(activity).toFloat()
            val viewAnimator = viewGroup.animate()
            viewAnimator?.run {
                    translationX(0F)
                    .setDuration(1200)
                    .setStartDelay(100)
                    .start()
            }
            show(viewGroup)
        }

    }


    private fun show(viewGroup: ViewGroup) {
        if (viewGroup.childCount > 0) {
            return
        }
        viewGroup.visibility = View.VISIBLE
        destroyable = AdManager.show(viewGroup, oid)
    }


    /**
     * 安排广告销毁程序.
     */
    private fun scheduleAdDestroy(lifecycle: Lifecycle) {
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                onPageDestroy()
            }
        })
    }

}