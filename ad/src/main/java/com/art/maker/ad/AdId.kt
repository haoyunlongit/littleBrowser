package com.art.maker.ad

import androidx.annotation.Keep

/**
 * 广告id和优先级.
 *
 * 优先级只影响展示顺序，不影响加载顺序.
 *
 * [priority] 数字越大，优先级越高
 *
 * @author yushaojian
 * @date 2021-02-03 16:09
 */
@Keep
data class AdId(val format: Int, val value: String, val priority: Int = 0)