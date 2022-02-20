package com.smart.browser.little.ui.categories

import com.art.maker.data.model.Category

/**
 * 带解锁状态的[Category].
 *
 * @author yushaojian
 * @date 2021-11-13 16:23
 */
data class StatefulCategory(val category: Category, var unlocked: Boolean)