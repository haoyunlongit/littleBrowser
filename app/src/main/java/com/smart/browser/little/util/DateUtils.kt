package com.smart.browser.little.util

import android.annotation.SuppressLint
import android.text.format.DateUtils
import com.smart.browser.little.BrowserApplication
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    @SuppressLint("ConstantLocale")
    private val SERVER_DATE_FORMAT =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
    private var format = SimpleDateFormat("HH:mm", Locale.getDefault())
    private fun fromServerFormat(created: String): Date? {
        val dateString = created.replace("Z", "+0000")
        return SERVER_DATE_FORMAT.parse(
            dateString,
            ParsePosition(0)
        )
    }

    /**
     * 获取系统语言跟随时间
     */
    private fun formatDateStampString(time: Long): String {
        val formatFlags =
//            DateUtils.FORMAT_ABBREV_ALL or
            DateUtils.FORMAT_SHOW_DATE or
                    DateUtils.FORMAT_SHOW_YEAR
//                    DateUtils.FORMAT_SHOW_WEEKDAY
        return DateUtils.formatDateTime(BrowserApplication.context, time, formatFlags);
    }

    fun formatDate(dateStr: String?): String {
        if (dateStr.isNullOrEmpty()) {
            return ""
        }
        try {
            val date = fromServerFormat(dateStr) ?: return dateStr
            return formatDateStampString(date.time)
        } catch (e: Exception) {
            return ""
        }
    }

    fun formatTime(dateStr: String?): String {
        if (dateStr.isNullOrEmpty()) {
            return ""
        }
        try {
            val date = fromServerFormat(dateStr) ?: return dateStr
            return format.format(date)
        } catch (e: Exception) {
            return ""
        }
    }
}