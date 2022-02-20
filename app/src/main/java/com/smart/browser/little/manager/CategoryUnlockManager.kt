package com.smart.browser.little.manager

import com.art.maker.util.SPUtils

var unlockingCategoryKey: String? = null

private const val UNLOCKED_CATEGORY = "unlocked_category"

fun isUnlockedCategory(categoryKey: String): Boolean {
    return SPUtils.getBoolean(formatUnlockedCategoryKey(categoryKey), false)
}

fun recordUnlockedCategory(categoryKey: String) {
    SPUtils.putBoolean(formatUnlockedCategoryKey(categoryKey), true)
}

private fun formatUnlockedCategoryKey(category: String) = UNLOCKED_CATEGORY + "_" + category
