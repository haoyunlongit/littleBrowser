package com.art.vd.model

import android.os.Parcelable
import androidx.annotation.Keep

/**
 * 视频信息.
 *
 * @author yushaojian
 * @date 2022-01-02 17:43
 */
@kotlinx.parcelize.Parcelize
@Keep
data class Video(
    val downloadUrl: String,
    val quality: String? = null,
    var size: Long = 0,
    val previewUrl: String? = null,
    val title: String? = null
) : Parcelable
