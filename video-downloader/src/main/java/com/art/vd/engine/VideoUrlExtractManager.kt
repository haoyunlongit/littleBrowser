package com.art.vd.engine

import com.art.vd.model.Video
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

/**
 * 视频url提取管理器.
 *
 * @author yushaojian
 * @date 2021-12-25 14:45
 */
internal class VideoUrlExtractManager {

    private val scope = MainScope()

    private val extracting: ArrayList<String> = ArrayList()
    private val extracted: ArrayList<Video> = ArrayList()

    private var extractListener: WeakReference<VideoUrlExtractListener>? = null

    fun setVideoUrlExtractListener(listener: VideoUrlExtractListener) {
        extractListener = WeakReference(listener)
    }

    fun enqueue(url: String) {
        if (extracting.contains(url) || extracted.any { it.downloadUrl == url }) return

        if (!maybeVideoUrl(url)) return

        extracting.add(url)
        scope.launch {
            val result = VideoResourceExtractor.extract(url)
            if (result != null) {
                extracted.addAll(result)
                result.forEach { extractListener?.get()?.onVideoUrlExtracted(extracted) }
            }

            extracting.remove(url)
        }
    }

    interface VideoUrlExtractListener {
        fun onVideoUrlExtracted(extracted: List<Video>)
    }
}