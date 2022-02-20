package com.smart.browser.little.ad

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smart.browser.little.databinding.AdNativeHomeBinding
import com.google.android.gms.ads.nativead.NativeAd

/**
 * 原生广告的ViewHolder.
 *
 * @author yushaojian
 * @date 2021-06-17 18:37
 */
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class NativeAdViewHolder(val binding: AdNativeHomeBinding) : RecyclerView.ViewHolder(binding.root) {

    init {
        // Set the media view.
        val adView = binding.adView
        adView.mediaView = binding.adMedia

        // Set other ad assets.
        adView.headlineView = binding.adHeadline
        adView.bodyView = binding.adBody
        adView.callToActionView = binding.adCta
        adView.iconView = binding.adIcon
    }
    
    fun bind(nativeAd: NativeAd) {
        // The headline and media content are guaranteed to be in every UnifiedNativeAd.
        binding.adHeadline.text = nativeAd.headline
        binding.adMedia.setMediaContent(nativeAd.mediaContent)

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            binding.adBody.visibility = View.GONE
        } else {
            binding.adBody.visibility = View.VISIBLE
            binding.adBody.text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            binding.adCta.visibility = View.GONE
        } else {
            binding.adCta.visibility = View.VISIBLE
            binding.adCta.text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            binding.adIcon.visibility = View.GONE
        } else {
            binding.adIcon.setImageDrawable(nativeAd.icon.drawable)
            binding.adIcon.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        binding.adView.setNativeAd(nativeAd)
    }

    companion object {
        fun from(parent: ViewGroup): NativeAdViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = AdNativeHomeBinding.inflate(layoutInflater, parent, false)
            return NativeAdViewHolder(binding)
        }
    }
}