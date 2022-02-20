package com.smart.browser.little.ui.search

import com.art.maker.data.model.Site

/**
 * 搜索结果item.
 *
 * @author yushaojian
 * @date 2021-06-20 16:22
 */
sealed class ResultItem

class SiteItem(val site: Site) : ResultItem()

class SuggestionItem(val site: Site, val query: String) : ResultItem()