package com.smart.browser.little.util

import android.content.Context
import com.smart.browser.little.BrowserApplication
import com.art.maker.util.SPUtils
import java.util.*

object LocalLanguageHelper {
    private const val LOCAL_LANGUAGE = "aio_local_language"
    fun getLanguage(): String {
        val languageStr = SPUtils.getString(LOCAL_LANGUAGE, "")
        if (!languageStr.isNullOrEmpty()) {
            return languageStr
        }
        return try {
            var lan = getCurrentLanguageUseResources(BrowserApplication.context)
            if (lan.isNullOrEmpty()) {
                lan = getCurrentLanguage()
            }
            if (!lan.isNullOrEmpty()) {
                SPUtils.putString(LOCAL_LANGUAGE, lan)
            }
            lan
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * 设置成简体中文的时候，getLanguage()返回的是zh
     */
    private fun getCurrentLanguage(): String {
        return Locale.getDefault().language
    }

    /**
     * 获得当前系统语言
     */
    private fun getCurrentLanguageUseResources(context: Context): String? {
        val locale = context.resources?.configuration?.locale
        return locale?.language
    }
}