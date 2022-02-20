package com.art.vd.engine

import android.os.Build
import android.util.Base64
import android.webkit.WebViewClient
import com.art.vd.model.Preview
import com.art.vd.model.Video
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.source
import org.json.JSONObject
import java.net.URL

/**
 * 基于[WebViewClient].onLoadResource的视频资源提取器.
 *
 * @author yushaojian
 * @date 2022-01-03 14:41
 */
internal object VideoResourceExtractor {

    private const val VIDEO_MP4 = "video/mp4"
    private const val MEGA = 1024 * 1024 // 1M

    private val VIDEO = "dmlkZW8=".keep { String(Base64.decode(this, Base64.DEFAULT)) }
    private val TITLE = "dGl0bGU=".keep { String(Base64.decode(this, Base64.DEFAULT)) }
    private val THUMBS = "dGh1bWJz".keep { String(Base64.decode(this, Base64.DEFAULT)) }
    private val REQUEST = "cmVxdWVzdA==".keep { String(Base64.decode(this, Base64.DEFAULT)) }
    private val FILES = "ZmlsZXM=".keep { String(Base64.decode(this, Base64.DEFAULT)) }
    private val PROGRESIVE = "cHJvZ3Jlc3NpdmU=".keep { String(Base64.decode(this, Base64.DEFAULT)) }
    private val QUALITY = "cXVhbGl0eQ==".keep { String(Base64.decode(this, Base64.DEFAULT)) }
    private val URL = "dXJs".keep { String(Base64.decode(this, Base64.DEFAULT)) }
    private val HEIGHT = "aGVpZ2h0".keep { String(Base64.decode(this, Base64.DEFAULT)) }
    private val POSTERS = "cG9zdGVycw==".keep { String(Base64.decode(this, Base64.DEFAULT)) }
    private val QUALITIES = "cXVhbGl0aWVz".keep { String(Base64.decode(this, Base64.DEFAULT)) }
    private val AUTO = "YXV0bw==".keep { String(Base64.decode(this, Base64.DEFAULT)) }
    private val VIMEO_FILTER = "aHR0cHM6Ly9wbGF5ZXIudmltZW8uY29tL3ZpZGVvLw==".keep { String(Base64.decode(this, Base64.DEFAULT)) }
    private val DAILYMOTION_FILTER = "aHR0cHM6Ly93d3cuZGFpbHltb3Rpb24uY29tL3BsYXllci9tZXRhZGF0YS92aWRlbw==".keep { String(Base64.decode(this, Base64.DEFAULT)) }

    suspend fun extract(resourceUrl: String): List<Video>? {
        if (isVimeo(resourceUrl)) {
            return extractVimeo(resourceUrl)
        }

        /*if (isDailymotion(resourceUrl)) {
            return extractDailymotion(resourceUrl)
        }*/
        return extractNormal(resourceUrl)
    }

    private suspend fun extractVimeo(resourceUrl: String): List<Video>? = withContext(Dispatchers.IO) {
        val configs = read(resourceUrl) ?: return@withContext null
        return@withContext parseVimeoVideos(configs)
    }

    private fun parseVimeoVideos(configs: String): List<Video>? {
        try {
            val jsonObject = JSONObject(configs)

            val videoObject = jsonObject.getJSONObject(VIDEO)
            val title = videoObject.getString(TITLE)
            val thumbs = videoObject.getJSONObject(THUMBS)
            val keys = thumbs.keys()
            val previews = ArrayList<Preview>()
            while (keys.hasNext()) {
                val key = keys.next()
                try {
                    val quality = key.toInt()
                    val value = thumbs.getString(key)
                    val preview = Preview(quality, value)
                    previews.add(preview)
                } catch (e: Exception) {
                    // skip non-int keys
                }
            }

            val videos = ArrayList<Video>()

            val progressive = jsonObject.getJSONObject(REQUEST).getJSONObject(FILES).getJSONArray(PROGRESIVE)
            val size = progressive.length()
            for (i in 0 until size) {
                val progressiveObject = progressive.getJSONObject(i)
                val quality = progressiveObject.getString(QUALITY)
                val downloadUrl = progressiveObject.getString(URL)
                val height = progressiveObject.getInt(HEIGHT)
                val preview = previews.firstOrNull { preview -> preview.quality >= height }
                videos.add(Video(downloadUrl, quality, 0, preview?.previewUrl, title))
            }

            return videos
        }catch (e: java.lang.Exception){
            e.printStackTrace()
        }
        return null

    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun extractDailymotion(resourceUrl: String): List<Video>? = withContext(Dispatchers.IO) {
        try {
            val metadata = getDailymotionMetadata(resourceUrl)
            val triple = parseDailymotionManifestUrl(metadata)
            val title = triple.first
            val previews = triple.second
            val manifest = getDailymotionManifest(triple.third)

            val groupsRegex = Regex("NAME=\"(\\d+)\".*(http.*\\.mp4)")
            val matches = groupsRegex.findAll(manifest)

            val videos = ArrayList<Video>()
            matches.forEach {
                val groupValues = it.groupValues
                val quality = groupValues[1]
                if (videos.none { video -> video.quality == quality }) { // dailymotion会给两组清晰度一样但url不一样的地址
                    val downloadUrl = groupValues[2]

                    val qualityInt = quality.toInt()
                    val preview = previews.firstOrNull { preview -> preview.quality >= qualityInt }

                    videos.add(Video(downloadUrl, quality, 0, preview?.previewUrl, title))
                }
            }

            videos
        } catch (e: Exception) {
            null
        }
    }

    private fun getDailymotionMetadata(url: String): String {
        val connection = URL(url).openConnection()
        val stream = connection.getInputStream()
        val source = stream.source().buffer()
        val s = source.readUtf8()
        source.close()
        return s
    }

    private fun parseDailymotionManifestUrl(s: String): Triple<String, List<Preview>, String> {
        val jsonObject = JSONObject(s)
        val title = jsonObject.getString(TITLE)

        val posters = jsonObject.getJSONObject(POSTERS)
        val keys = posters.keys()
        val previews = ArrayList<Preview>()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = posters.getString(key)
            val preview = Preview(key.toInt(), value)
            previews.add(preview)
        }

        val qualities = jsonObject.getJSONObject(QUALITIES)
        val auto = qualities.getJSONArray(AUTO)
        val first = auto.getJSONObject(0)
        val url = first.getString(URL)

        return Triple(title, previews, url)
    }

    private fun getDailymotionManifest(url: String): String {
        val connection = URL(url).openConnection()
        val stream = connection.getInputStream()
        val source = stream.source().buffer()
        val s = source.readUtf8()
        source.close()
        return s
    }

    private fun read(url: String): String? = try {
        val client = OkHttpClient()
        val request: Request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        response.body()?.string()
    } catch (e: Exception) {
        null
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun extractNormal(url: String): List<Video>? = withContext(Dispatchers.IO) {
        try {
            val connection = URL(url).openConnection()
            connection.connect()
            val contentType = connection.contentType

            // 暂时只支持mp4且大于1M的视频
            if (isVideo(contentType) && bigEnough(connection.contentLength)) {
                val size = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    connection.contentLengthLong
                } else {
                    connection.contentLength.toLong()
                }
                listOf(Video(url, size = size))
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun isVideo(contentType: String?) = VIDEO_MP4.equals(contentType, true)

    private fun bigEnough(size: Int) = size >= MEGA

    private fun isVimeo(resourceUrl: String) = resourceUrl.contains(VIMEO_FILTER)

    private fun isDailymotion(resourceUrl: String) = resourceUrl.contains(DAILYMOTION_FILTER)
}