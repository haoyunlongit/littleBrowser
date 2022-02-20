package com.art.maker.ad

import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.nativead.NativeAd

sealed class Destroyable(private val oid: String, private val adId: AdId) {

    fun destroy() {
        onDestroyed()
        Log.d("$oid ${getReportedAdID(adId.value)} priority ${adId.priority} destroyed")
    }

    protected abstract fun onDestroyed()

    class DestroyableNativeAd(private val nativeAd: NativeAd, oid: String, adId: AdId) : Destroyable(oid, adId) {
        override fun onDestroyed() {
            nativeAd.destroy()
        }
    }

    class DestroyableBannerAd(private val adView: AdView, oid: String, adId: AdId) : Destroyable(oid, adId) {
        override fun onDestroyed() {
            adView.destroy()
        }
    }
}