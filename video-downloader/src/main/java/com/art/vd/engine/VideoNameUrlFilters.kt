package com.art.vd.engine

private val VIDEO_URL_FILTERS = listOf("mp4", "video")

internal fun maybeVideoUrl(url: String) = VIDEO_URL_FILTERS.any { url.lowercase().contains(it) }