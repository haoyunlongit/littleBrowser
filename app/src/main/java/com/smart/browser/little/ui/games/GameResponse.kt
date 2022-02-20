package com.smart.browser.little.ui.games

import androidx.annotation.Keep

@Keep
data class GameResponse(
    val code: String,
    val `data`: Data,
    val msg: String
)