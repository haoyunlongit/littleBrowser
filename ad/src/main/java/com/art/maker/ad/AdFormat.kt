package com.art.maker.ad

import androidx.annotation.IntDef
import com.art.maker.ad.AdFormat.Companion.APP_OPEN
import com.art.maker.ad.AdFormat.Companion.INTERSTITIAL
import com.art.maker.ad.AdFormat.Companion.REWARD_VIDEO

/**
 * 广告格式.
 *
 * @author yushaojian
 * @date 2021-02-03 16:09
 */
@IntDef(INTERSTITIAL, REWARD_VIDEO, APP_OPEN)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class AdFormat {
    companion object {
        // AdMob
        const val INTERSTITIAL = 0        // AdMob插屏
        const val REWARD_VIDEO = 1        // AdMob激励视频
        const val APP_OPEN = 2            // AdMob App Open开屏
        const val REWARD_INTERSTITIAL = 3 // AdMob激励插屏
        const val NATIVE = 4              // AdMob原生
        const val NATIVE_INTERSTITIAL = 5 // AdMob原生，改插屏
        const val BANNER = 6              // AdMob Banner
        const val BANNER_ADAPTIVE = 7     // AdMob 自适应banner
    }
}