package com.art.maker.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * app信息.
 *
 * @author yushaojian
 * @date 2021-05-05 15:06
 */
@Parcelize
@Keep
data class Site(val name: String, val icon: String, val url: String): Parcelable
