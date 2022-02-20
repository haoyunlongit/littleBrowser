package com.art.vd.engine

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.Keep
import com.art.vd.model.Video

/**
 * 视频url提取WebViewClient.
 *
 * @author yushaojian
 * @date 2022-01-03 16:17
 */
@Keep
open class VideoUrlExtractClient : WebViewClient(), VideoUrlExtractManager.VideoUrlExtractListener {

    private val videoUrlExtractor = VideoUrlExtractManager()

    init {
        videoUrlExtractor.setVideoUrlExtractListener(this)
    }

    override fun onLoadResource(view: WebView, url: String) {
        super.onLoadResource(view, url)
        videoUrlExtractor.enqueue(url)
    }

    override fun onVideoUrlExtracted(extracted: List<Video>) {
    }
}