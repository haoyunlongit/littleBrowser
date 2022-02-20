package com.art.maker.util

import java.util.*

fun isYoutube(url: String?): Boolean {
    if (url.isNullOrBlank()) return false

    val lowercase = url.lowercase(Locale.ENGLISH)
    return lowercase.contains("youtu")
            || lowercase.contains("google")
            || lowercase.contains("gstatic")
            || lowercase.contains("ytimg")
}