package com.smart.browser.little.ui.games

import androidx.annotation.Keep

@Keep
data class Data(
    val count: Int,
    val list: List<Game>
)