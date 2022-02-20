package com.art.maker.ad

import android.app.Activity
import android.content.Context
import android.os.SystemClock
import android.util.DisplayMetrics
import android.view.WindowManager
import com.art.maker.ad.AdFormat.Companion.NATIVE
import com.google.android.gms.ads.*
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback

/**
 * 广告加载器，负责调用AdMob加载广告.
 *
 * @author yushaojian
 * @date 2021-02-03 16:09
 */
internal class AdLoader(
    private val oid: String,
    private val adId: AdId,
    @AdFormat private val adFormat: Int,
    private val adListener: InternalAdListener
) {

    private var loadCalled = false

    //-----------------------------API---------------------------------

    fun load(activity: Activity) {
        if (loadCalled) {
            Log.e("load can only be called once")
            return
        }

        loadCalled = true
        Log.e("reloadAdIfNull","start load $adFormat")
        when (adFormat) {
            AdFormat.INTERSTITIAL -> loadInterstitial(activity)
            AdFormat.REWARD_VIDEO -> loadRewardVideo(activity)
            AdFormat.REWARD_INTERSTITIAL -> loadRewardInterstitial(activity)
            AdFormat.APP_OPEN -> loadAppOpen(activity)
            NATIVE, AdFormat.NATIVE_INTERSTITIAL -> loadNative(activity, AdManager.getNativeCustom(oid))
            AdFormat.BANNER -> loadBanner(activity, AdSize.MEDIUM_RECTANGLE, AdFormat.BANNER)
            AdFormat.BANNER_ADAPTIVE -> loadBanner(activity, getAdaptiveBannerAdSize(activity), AdFormat.BANNER_ADAPTIVE)
            else -> adListener.onAdLoadError(oid, adId, "unsupported ad format $adFormat")
        }
    }

    //-----------------------------AdMob---------------------------------

    private fun loadInterstitial(activity: Activity) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(activity, adId.value, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                onLoaded(interstitialAd)
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                onFailed(adError)
            }
        })

        Log.d("$oid ${getReportedAdID(adId.value)} priority ${adId.priority} start loading interstitial Ad")
    }

    private fun loadRewardVideo(activity: Activity) {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(activity, adId.value, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(rewardedAd: RewardedAd) {
                onLoaded(rewardedAd)
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                onFailed(loadAdError)
            }
        })

        Log.d("$oid ${getReportedAdID(adId.value)} priority ${adId.priority} start loading rewarded Ad")
    }

    private fun loadRewardInterstitial(activity: Activity) {
        val adRequest = AdRequest.Builder().build()
        RewardedInterstitialAd.load(activity, adId.value, adRequest, object : RewardedInterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedInterstitialAd) {
                onLoaded(ad)
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                onFailed(loadAdError)
            }
        })

        Log.d("$oid ${getReportedAdID(adId.value)} priority ${adId.priority} start loading rewarded Ad")
    }

    private fun loadAppOpen(activity: Activity) {
        val adRequest = AdRequest.Builder().build()
        AppOpenAd.load(activity, adId.value, adRequest, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(appOpenAd: AppOpenAd) {
                    onLoaded(appOpenAd)
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    onFailed(adError)
                }
            })

        Log.d("$oid ${getReportedAdID(adId.value)} priority ${adId.priority} start loading app open Ad")
    }

    private fun loadNative(activity: Activity, custom: NativeCustom? = null) {
        val builder = AdLoader.Builder(activity, adId.value)
        val videoOptions = VideoOptions.Builder()
            .setStartMuted(custom?.videoStartMuted() ?: false)
            .build()

        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .setReturnUrlsForImageAssets(custom?.shouldReturnUrlsForImageAssets() ?: false)
            .setMediaAspectRatio(custom?.getMediaAspectRatio() ?: NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_UNKNOWN)
            .setRequestMultipleImages(custom?.shouldRequestMultipleImages() ?: false)
            .setAdChoicesPlacement(custom?.getAdChoicesPlacement() ?: NativeAdOptions.ADCHOICES_TOP_RIGHT)
            .build()

        builder.withNativeAdOptions(adOptions)
        builder.withAdListener(object : com.google.android.gms.ads.AdListener() {

            override fun onAdLoaded() {
                // onNativeAdLoaded的冗余回调
            }

            override fun onAdFailedToLoad(adError: LoadAdError?) { // 广告加载失败时回调
                adError?.let { onFailed(it) }
            }

            override fun onAdImpression() { // 广告展示
                Log.d("$oid ${getReportedAdID(adId.value)} priority ${adId.priority} onAdImpression")
                adListener.onAdOpened(oid)
                AdManager.adReporter?.onAdImpression(oid, NATIVE)
            }

            override fun onAdClicked() { // 广告点击时调用
                Log.d("$oid ${getReportedAdID(adId.value)} priority ${adId.priority} onAdClicked")
            }

            override fun onAdOpened() { // 广告点击后跳转时调用
                Log.d("$oid ${getReportedAdID(adId.value)} priority ${adId.priority} onAdOpened")
            }

            override fun onAdClosed() { // 广告跳转的页面关闭后调用
                Log.d("$oid ${getReportedAdID(adId.value)} priority ${adId.priority} onAdClosed")
                adListener.onAdClosed(oid)
            }
        })
        builder.forNativeAd { nativeAd ->
            onLoaded(nativeAd)
        }
        builder.build().loadAd(AdRequest.Builder().build())
        Log.d("$oid ${getReportedAdID(adId.value)} priority ${adId.priority} start loading native Ad")
    }

    private fun loadBanner(activity: Activity, adSize: AdSize, adFormat: Int) {
        val adView = AdView(activity.applicationContext)
        adView.adSize = adSize
        adView.adUnitId = adId.value
        adView.adListener = object : com.google.android.gms.ads.AdListener() {
            var onAdLoadedCalled: Boolean = false
            override fun onAdLoaded() {
                if(!onAdLoadedCalled){
                    onAdLoadedCalled = true
                    onLoaded(adView)
                }
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                if(!onAdLoadedCalled){
                    onAdLoadedCalled = true
                    onFailed(adError)
                }
            }

            override fun onAdClicked() {
                Log.d("$oid ${getReportedAdID(adId.value)} priority ${adId.priority} onAdClicked")
            }

            override fun onAdOpened() {
                Log.d("$oid ${getReportedAdID(adId.value)} priority ${adId.priority} onAdOpened")
            }

            override fun onAdImpression() {
                //回调不准弃用
                /*Log.d("$oid ${getReportedAdID(adId.value)} priority ${adId.priority} onAdImpression")
                adListener.onAdOpened(oid)
                AdManager.adReporter?.onAdImpression(oid, adId.format)*/
            }

            override fun onAdClosed() {
                Log.d("$oid ${getReportedAdID(adId.value)} priority ${adId.priority} onAdOpened")
                adListener.onAdClosed(oid)
            }
        }
        adView.loadAd(AdRequest.Builder().build())
        Log.d("$oid ${getReportedAdID(adId.value)} priority ${adId.priority} start loading banner Ad")
    }

    //-----------------------------Callback------------------------------

    private fun onLoaded(ad: Any) {
        Log.d("$oid ${getReportedAdID(adId.value)} priority ${adId.priority} onAdLoaded")
        adListener.onAdLoaded(oid, AdModel(oid, adId, ad, SystemClock.elapsedRealtime()))
    }

    private fun onFailed(adError: LoadAdError) {
        Log.d("$oid ${getReportedAdID(adId.value)} priority ${adId.priority} onAdFailedToLoad $adError")
        adListener.onAdLoadError(oid, adId, adError.toString())
    }

    private fun onFailed(error: String) {
        Log.d("$oid ${getReportedAdID(adId.value)} priority ${adId.priority} onAdFailedToLoad $error")
        adListener.onAdLoadError(oid, adId, error)
    }


    private fun getAdaptiveBannerAdSize(context: Context): AdSize{
        val metric = DisplayMetrics()
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            .defaultDisplay
            .getMetrics(metric)
        val density = metric.density

        var adWidthPixels = metric.widthPixels
        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
    }

}