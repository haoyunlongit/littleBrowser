package com.art.maker.ad

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import com.art.maker.ad.AdModel.Companion.showNative
import com.art.maker.ad.databinding.NativeInterstitialAdActivityBinding
import com.google.android.gms.ads.nativead.NativeAd


/**
 * 原生改插屏Activity.
 *
 * @author yushaojian
 * @date 2021-06-20 07:06
 */
class NativeInterstitialAdActivity : Activity() {

    private var closePending = false

    private val adListener = object : AdListener() {
        override fun onAdClosed(oid: String) {
            super.onAdClosed(oid)
            closePending = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = NativeInterstitialAdActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val oid = oid
        if (oid == null) {
            finish()
            return
        } else {
            AdManager.addAdListener(oid, adListener)
        }

        val ad = nativeAd
        if (ad == null) {
            finish()
            return
        }

        showNative(binding.adLayout, ad, nativeCustom ?: NativeInterstitialCustom())

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

    override fun onResume() {
        super.onResume()
        if (closePending) {
            finish()
        }
    }

    override fun onDestroy() {
        oid?.let {
            internalAdListener?.run { onAdClosed(it) }
            AdManager.removeAdListener(it, adListener)
        }
        oid = null
        nativeAd?.destroy()
        nativeAd = null
        nativeCustom = null
        internalAdListener = null
        super.onDestroy()
    }

    companion object {

        private var oid: String? = null
        private var nativeAd: NativeAd? = null
        private var nativeCustom: NativeCustom? = null
        private var internalAdListener: InternalAdListener? = null

        internal fun show(
            activity: Activity, oid: String, nativeAd: NativeAd, nativeCustom: NativeCustom?,
            internalAdListener: InternalAdListener
        ) {
            this.oid = oid
            this.nativeAd = nativeAd
            this.nativeCustom = nativeCustom
            this.internalAdListener = internalAdListener
            activity.startActivity(Intent(activity, NativeInterstitialAdActivity::class.java))
        }

    }
}