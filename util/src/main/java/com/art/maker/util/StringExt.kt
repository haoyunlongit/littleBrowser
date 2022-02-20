package com.art.maker.util

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

fun String.isValidHeader(): Boolean {
    val size = length
    for (i in 0 until size) {
        val c = get(i)
        if (c <= '\u001f' || c >= '\u007f') {
            return false
        }
    }
    return true
}

fun String?.md5(): String {
    if (this.isNullOrEmpty()) return ""
    return try {
        val digest = MessageDigest.getInstance("MD5")
        val result = digest.digest(this.toByteArray())
        toHex(result)
    } catch (e: NoSuchAlgorithmException) {
        ""
    }
}

fun String?.sha1(): String {
    if (this.isNullOrEmpty()) return ""
    return try {
        val digest = MessageDigest.getInstance("SHA-1")
        val result = digest.digest(this.toByteArray())
        toHex(result)
    } catch (e: NoSuchAlgorithmException) {
        ""
    }
}

private fun toHex(bytes: ByteArray?): String {
    if (bytes == null || bytes.isEmpty()) {
        return ""
    }
    val sb = StringBuffer()
    for (offset in bytes.indices) {
        val b = bytes[offset].toInt() and 0xFF
        if (b < 0x10) sb.append("0")
        sb.append(Integer.toHexString(b))
    }
    return sb.toString()
}