package com.art.maker.data.model

import androidx.annotation.Keep

/**
 * app分类.
 *
 * @author yushaojian
 * @date 2021-05-05 15:05
 */
@Keep
data class SiteSection(val name: String, val key: String? = null, val sites: List<Site>)