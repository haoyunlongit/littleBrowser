package com.smart.browser.little.ui.games

import androidx.annotation.Keep

@Keep
data class Game(
    val Id: Int,
    val banner: List<String>,
    val control: String,
    val descs: String,
    val detailurl: String,
    val gameurl: String,
    val height: Int,
    val icon: String,
    val language: List<String>,
    val name: String,
    val screenshot: List<String>,
    val tage: List<String>,
    val width: Int
)