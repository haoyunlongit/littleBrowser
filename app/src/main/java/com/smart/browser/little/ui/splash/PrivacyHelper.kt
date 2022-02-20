package com.smart.browser.little.ui.splash

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.core.content.ContextCompat
import com.smart.browser.little.R
import com.art.maker.util.SPUtils
import com.art.maker.util.buildHighlightContent

/**
 * 隐私政策相关逻辑.
 *
 * @author yushaojian
 * @date 2021-06-20 11:21
 */

private const val PRIVACY_POLICY_ACCEPTED = "privacy_policy_accepted"

fun privacyPolicyNotAccepted() = !SPUtils.getBoolean(PRIVACY_POLICY_ACCEPTED, false)

fun setPrivacyPolicyAccepted() = SPUtils.putBoolean(PRIVACY_POLICY_ACCEPTED, true)

fun buildPrivacyPolicyContent(context: Context, open: (Context, String) -> Unit): SpannableStringBuilder {
    val content = context.getString(R.string.privacy_policy_content)
    val highlight = context.getString(R.string.privacy_policy)
    val url = context.getString(R.string.privacy_policy_url)
    val color = ContextCompat.getColor(context, R.color.teal_200)

    return buildHighlightContent(content, highlight, color, url, open)
}