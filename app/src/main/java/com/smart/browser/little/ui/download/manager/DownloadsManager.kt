package com.smart.browser.little.ui.download.manager

import android.os.Environment
import androidx.core.content.ContextCompat
import com.smart.browser.little.BrowserApplication
import com.smart.browser.little.BuildConfig
import com.smart.browser.little.report.reportEvent
import com.art.maker.util.Log
import com.art.maker.util.download
import com.art.vd.model.Video
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.File
import java.lang.ref.WeakReference

/**
 * 下载管理.
 *
 * @author yushaojian
 * @date 2022-01-03 19:12
 */
object DownloadsManager {

    private const val VIDEOS_JSON = "downloaded_videos.json"
    private val scope = MainScope()

    private val gson by lazy { Gson() }

    private val downloadedVideos = ArrayList<Video>()

    private var downloadCallback: WeakReference<DownloadCallback>? = null

    init {
        scope.launch { downloadedVideos.addAll(read()) }
    }

    fun setDownloadCallback(callback: DownloadCallback?) {
        if (callback != null) {
            downloadCallback = WeakReference(callback)
        } else {
            downloadCallback = null
        }
    }

    fun downloadVideos(videos: List<Video>) {
        videos.forEach { downloadVideo(it) }
    }

    private fun downloadVideo(video: Video) {
        downloadCallback?.get()?.onDownloadStart(video)

        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
            downloadCallback?.get()?.onDownloadFailed(video)
        }

        scope.launch(exceptionHandler) {
            val folder = ContextCompat.getExternalFilesDirs(BrowserApplication.context, Environment.DIRECTORY_MOVIES).firstOrNull()
            if (folder == null) {
                downloadCallback?.get()?.onDownloadFailed(video)
                return@launch
            }

            val file = File(folder, "${System.currentTimeMillis()}.mp4")
            download(video.downloadUrl, file)
            val title = video.title ?: file.name
            val localVideo = video.copy(downloadUrl = file.absolutePath, size = file.length(), title = title)
            if (BuildConfig.DEBUG) {
                Log.d("downloaded $localVideo")
            }
            downloadedVideos.add(0, localVideo)
            write(downloadedVideos)

            downloadCallback?.get()?.onDownloadCompleted(video)
            reportEvent("video_download_complete", extra = video.downloadUrl)
        }
    }

    suspend fun read(): List<Video>  {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(BrowserApplication.context.filesDir, VIDEOS_JSON)
                val text = file.readText()
                val videos: List<Video> = gson.fromJson(text, object : TypeToken<List<Video>>() {}.type)
                if (BuildConfig.DEBUG) {
                    Log.d("read $videos")
                }
                videos.filter { File(it.downloadUrl).exists() }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    private suspend fun write(videos: List<Video>) = withContext(Dispatchers.IO) {
        if (BuildConfig.DEBUG) {
            Log.d("write $videos")
        }
        try {
            val text = gson.toJson(videos)
            val file = File(BrowserApplication.context.filesDir, VIDEOS_JSON)
            file.writeText(text)
        } catch (e: Exception) {
        }
    }

    interface DownloadCallback {

        fun onDownloadStart(video: Video)

        fun onDownloadCompleted(video: Video)

        fun onDownloadFailed(video: Video)
    }
}