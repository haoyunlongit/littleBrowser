package com.art.maker.util

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View

fun buildHighlightContent(content: String, highlight: String, color: Int, url: String, open: (Context, String) -> Unit)
    : SpannableStringBuilder {
    val stringBuilder = SpannableStringBuilder(content)

    try {
        val start = content.indexOf(highlight)
        val end = start + highlight.length

        stringBuilder.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                open.invoke(widget.context, url)
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val foregroundColorSpan = ForegroundColorSpan(color)
        stringBuilder.setSpan(foregroundColorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    } catch (e: Exception) {
    }

    return stringBuilder
}

/**
 * 文本局部高亮.
 *
 * @param content 完整文本
 * @param subString 要高亮的文本
 * @param color 高亮色
 */
@Suppress("unused")
fun highlightSubString(content: String, subString: String, color: Int): SpannableStringBuilder {
    val stringBuilder = SpannableStringBuilder(content)

    val index = content.indexOf(subString)
    if (index == -1) return stringBuilder

    val foregroundColorSpan = ForegroundColorSpan(color)
    stringBuilder.setSpan(foregroundColorSpan, index, index + subString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    return stringBuilder
}
