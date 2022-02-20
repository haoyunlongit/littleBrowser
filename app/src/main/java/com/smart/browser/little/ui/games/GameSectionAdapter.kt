package com.smart.browser.little.ui.games

import com.smart.browser.little.data.R

/**
 * 游戏列表适配器.
 *
 * @author yushaojian
 * @date 2021-06-06 10:52
 */
open class GameSectionAdapter : AdvertSectionAdapter() {
    override val sectionLayoutId = R.layout.item_game_section
    override val itemLayoutId = R.layout.item_game
}