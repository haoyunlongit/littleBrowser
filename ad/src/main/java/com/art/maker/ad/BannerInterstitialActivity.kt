package com.art.maker.ad

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.art.maker.ad.databinding.BannerInterstitialAdActivityBinding
import com.google.android.gms.ads.AdView

/**
 * Banner插屏页.
 *
 * @author yushaojian
 * @date 2021-11-21 21:33
 */
class BannerInterstitialActivity  : Activity() {

    private var adListener: AdListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = BannerInterstitialAdActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val oid = oid
        if (oid == null) {
            finish()
            return
        }

        val bannerView = bannerView
        if (bannerView == null) {
            finish()
            return
        }

        val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.CENTER
        binding.adLayout.addView(bannerView, lp)

        val listener = object : AdListener() {
            override fun onAdShowed(oid: String) {
                binding.loadingPB.visibility = View.GONE
            }
        }
        AdManager.addAdListener(oid, listener)
        adListener = listener

        try {
            val packageManager = packageManager
            val info = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            binding.appIconIV.setImageDrawable(packageManager.getApplicationIcon(packageName))
            binding.appNameTV.text = packageManager.getApplicationLabel(info).toString()
        } catch (e: Exception) {
        }

        binding.closeIV.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        bannerView?.destroy()
        bannerView = null
        oid?.let { oid ->
            internalAdListener?.run { onAdClosed(oid) }
            adListener?.let { AdManager.removeAdListener(oid, it) }
        }
        internalAdListener = null
        super.onDestroy()
    }

    companion object {

        private var oid: String? = null
        private var bannerView: AdView? = null
        private var internalAdListener: InternalAdListener? = null

        internal fun show(activity: Activity, oid: String, bannerView: AdView,
                          internalAdListener: InternalAdListener) {
            this.oid = oid
            this.bannerView = bannerView
            this.internalAdListener = internalAdListener
            activity.startActivity(Intent(activity, BannerInterstitialActivity::class.java))
        }
    }
}