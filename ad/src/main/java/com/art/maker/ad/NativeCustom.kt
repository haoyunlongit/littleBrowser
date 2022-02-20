@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.art.maker.ad

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.art.maker.ad.databinding.NativeInterstitialAdBinding
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions.ADCHOICES_TOP_RIGHT
import com.google.android.gms.ads.nativead.NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_UNKNOWN

/**
 * 原生广告自定义.
 *
 * @author yushaojian
 * @date 2021-04-15 14:01
 */
interface NativeCustom {

    fun shouldReturnUrlsForImageAssets(): Boolean = false

    fun getMediaAspectRatio(): Int = NATIVE_MEDIA_ASPECT_RATIO_UNKNOWN

    fun shouldRequestMultipleImages(): Boolean = false

    fun videoStartMuted(): Boolean = false

    fun getAdChoicesPlacement(): Int = ADCHOICES_TOP_RIGHT

    /**
     * @return true表示处理了，false表示未处理
     */
    fun showNative(viewGroup: ViewGroup, nativeAd: NativeAd): Boolean = false

}

class NativeInterstitialCustom : NativeCustom {
    override fun showNative(viewGroup: ViewGroup, nativeAd: NativeAd): Boolean {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val binding = NativeInterstitialAdBinding.inflate(layoutInflater, viewGroup, false)
        populateNativeAdView(nativeAd, binding)
        viewGroup.removeAllViews()
        viewGroup.addView(binding.root)
        return true
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adNativeBinding: NativeInterstitialAdBinding) {
        val adView = adNativeBinding.adView

        // Set the media view.
        adView.mediaView = adNativeBinding.adMedia

        // Set other ad assets.
        adView.headlineView = adNativeBinding.adHeadline
        adView.bodyView = adNativeBinding.adBody
        adView.callToActionView = adNativeBinding.adCta
        adView.iconView = adNativeBinding.adIcon

        // optional
        adView.starRatingView = adNativeBinding.adStars

        // The headline and media content are guaranteed to be in every UnifiedNativeAd.
        adNativeBinding.adHeadline.text = nativeAd.headline

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

        if (nativeAd.starRating == null) {
            adNativeBinding.adStars.visibility = View.GONE
        } else {
            adNativeBinding.adStars.rating = nativeAd.starRating.toFloat()
            adNativeBinding.adStars.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)
    }
}