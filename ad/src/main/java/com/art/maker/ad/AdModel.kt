package com.art.maker.ad

import android.app.Activity
import android.os.SystemClock
import android.text.format.DateUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd

/**
 * 广告对象封装，包含广告过期逻辑.
 *
 * @author yushaojian
 * @date 2021-02-03 16:09
 */
@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
internal data class AdModel(private val oid: String, val adId: AdId, private val ad: Any, private val loadedTime: Long) {

    internal fun show(activity: Activity, listener: InternalAdListener): Boolean {
        var adFormat = -1
        val callback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e("$oid  ${getReportedAdID(adId.value)} priority ${adId.priority} onAdFailedToShowFullScreenContent $adError")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("$oid ${getReportedAdID(adId.value)} priority ${adId.priority} onAdOpened")
                listener.onAdOpened(oid)
                AdManager.adReporter?.onAdImpression(oid, adFormat)
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d("$oid ${getReportedAdID(adId.value)} priority ${adId.priority} onAdClosed")
                listener.onAdClosed(oid)
            }
        }

        when (ad) {
            is InterstitialAd -> {
                adFormat = AdFormat.INTERSTITIAL
                ad.fullScreenContentCallback = callback
                ad.show(activity)
                return true
            }
            is RewardedAd -> {
                adFormat = AdFormat.REWARD_VIDEO
                ad.fullScreenContentCallback = callback
                ad.show(activity, OnUserEarnedRewardListener {
                    Log.d("$oid ${getReportedAdID(adId.value)} priority ${adId.priority} onUserEarnedReward")
                    listener.onRewardEarned(oid)
                })
                return true
            }
            is RewardedInterstitialAd -> {
                adFormat = AdFormat.REWARD_INTERSTITIAL
                ad.fullScreenContentCallback = callback
                ad.show(activity, OnUserEarnedRewardListener {
                    Log.d("$oid ${getReportedAdID(adId.value)} priority ${adId.priority} onUserEarnedReward")
                    listener.onRewardEarned(oid)
                })
                return true
            }
            is AppOpenAd -> {
                adFormat = AdFormat.APP_OPEN
                ad.fullScreenContentCallback = callback
                ad.show(activity)
                return true
            }
            is NativeAd -> {
                NativeInterstitialAdActivity.show(activity, oid, ad, AdManager.getNativeCustom(oid), listener)
                return true
            }
            is AdView -> {
                Log.d("$oid ${getReportedAdID(adId.value)} priority ${adId.priority} onAdImpression")
                BannerInterstitialActivity.show(activity, oid, ad, listener)
                listener.onAdOpened(oid)
                AdManager.adReporter?.onAdImpression(oid, adId.format)
                return true
            }
            else -> {
                Log.w("$oid ${getReportedAdID(adId.value)} priority ${adId.priority} show failed, format not supported")
                return false
            }
        }
    }

    internal fun show(viewGroup: ViewGroup, nativeCustom: NativeCustom? = null, listener: InternalAdListener): Destroyable? {
        if (ad is NativeAd) {
            showNative(viewGroup, ad, nativeCustom)
            return Destroyable.DestroyableNativeAd(ad, oid, adId)
        }

        if(ad is AdView){
            Log.d("$oid ${getReportedAdID(adId.value)} priority ${adId.priority} onAdImpression")
            showBannerAd(viewGroup, ad)
            listener.onAdOpened(oid)
            AdManager.adReporter?.onAdImpression(oid, adId.format)
            return Destroyable.DestroyableBannerAd(ad, oid, adId)
        }
        return null
    }

    internal fun getAd(): Any? {
        return if (isValid()) ad else null
    }

    internal fun isValid(): Boolean {
        val valid = SystemClock.elapsedRealtime() - loadedTime <= VALID_DURATION
        if (!valid) return false
        return valid
    }

    companion object {
        private const val VALID_DURATION = DateUtils.MINUTE_IN_MILLIS * 50

        internal fun showNative(viewGroup: ViewGroup, nativeAd: NativeAd, nativeCustom: NativeCustom? = null) {
            if (nativeCustom != null) {
                val handled = nativeCustom.showNative(viewGroup, nativeAd)
                if (handled) return
            }

            val layoutInflater = LayoutInflater.from(viewGroup.context)
            val adView = layoutInflater.inflate(R.layout.ad_unified, viewGroup, false) as NativeAdView
            populateNativeAdView(nativeAd, adView)
            viewGroup.removeAllViews()
            viewGroup.addView(adView)
        }


        /**
         * Populates a [NativeAdView] object with data from a given
         * [NativeAd].
         *
         * @param nativeAd the object containing the ad's assets
         * @param adView the view to be populated
         */
        private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
            // Set the media view.
            adView.mediaView = adView.findViewById(R.id.adMedia)

            // Set other ad assets.
            adView.headlineView = adView.findViewById(R.id.adHeadline)
            adView.bodyView = adView.findViewById(R.id.adBody)
            adView.callToActionView = adView.findViewById(R.id.adCta)
            adView.iconView = adView.findViewById(R.id.adIcon)

            // optional
            adView.priceView = adView.findViewById(R.id.ad_price)
            adView.starRatingView = adView.findViewById(R.id.ad_stars)
            adView.storeView = adView.findViewById(R.id.ad_store)
            adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

            // The headline and media content are guaranteed to be in every UnifiedNativeAd.
            (adView.headlineView as TextView).text = nativeAd.headline
            adView.mediaView.setMediaContent(nativeAd.mediaContent)

            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
            if (nativeAd.body == null) {
                adView.bodyView.visibility = View.INVISIBLE
            } else {
                adView.bodyView.visibility = View.VISIBLE
                (adView.bodyView as TextView).text = nativeAd.body
            }

            if (nativeAd.callToAction == null) {
                adView.callToActionView.visibility = View.INVISIBLE
            } else {
                adView.callToActionView.visibility = View.VISIBLE
                (adView.callToActionView as Button).text = nativeAd.callToAction
            }

            if (nativeAd.icon == null) {
                adView.iconView.visibility = View.GONE
            } else {
                (adView.iconView as ImageView).setImageDrawable(
                    nativeAd.icon.drawable
                )
                adView.iconView.visibility = View.VISIBLE
            }

            if (nativeAd.price == null) {
                adView.priceView.visibility = View.INVISIBLE
            } else {
                adView.priceView.visibility = View.VISIBLE
                (adView.priceView as TextView).text = nativeAd.price
            }

            if (nativeAd.store == null) {
                adView.storeView.visibility = View.INVISIBLE
            } else {
                adView.storeView.visibility = View.VISIBLE
                (adView.storeView as TextView).text = nativeAd.store
            }

            if (nativeAd.starRating == null) {
                adView.starRatingView.visibility = View.INVISIBLE
            } else {
                (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
                adView.starRatingView.visibility = View.VISIBLE
            }

            if (nativeAd.advertiser == null) {
                adView.advertiserView.visibility = View.INVISIBLE
            } else {
                (adView.advertiserView as TextView).text = nativeAd.advertiser
                adView.advertiserView.visibility = View.VISIBLE
            }

            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad view with this native ad.
            adView.setNativeAd(nativeAd)
        }


        internal fun showBannerAd(viewGroup: ViewGroup, adView: AdView) {
            viewGroup.removeAllViews()
            viewGroup.addView(adView)
        }
    }
}
