package com.art.maker.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import java.io.File

@Suppress("BlockingMethodInNonBlockingContext")
suspend fun download(url: String, file: File): Unit = withContext(Dispatchers.IO) {
    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()
    client.newCall(request).execute().use { response ->
        val body = response.body()
        val sink = file.sink().buffer()
        body?.source().use { input ->
            sink.use { output ->
                if (input != null) {
                    output.writeAll(input)
                } else {
                    file.delete()
                }
            }
        }
    }
}