package com.art.maker.util

import android.content.Context
import android.content.res.Resources.getSystem
import android.graphics.Point
import android.os.Build
import android.view.WindowManager

val screenWidth = getSystem().displayMetrics.widthPixels
private var screenHeight = 0
private var screenRatio = 0f

fun screenHeight(context: Context): Int {
    if (screenHeight == 0) { // 首次计算
        val applicationContext = context.applicationContext
        val windowManager =
            applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val point = Point()
        display.getRealSize(point)
        screenHeight = point.y
    }

    return screenHeight
}

fun screenRatio(context: Context): Float {
    if (screenRatio == 0f) {
        screenRatio = screenWidth.toFloat() / screenHeight(context).toFloat()
    }

    return screenRatio
}

@Suppress("unused")
val Int.dp: Float get() = this * getSystem().displayMetrics.density

@Suppress("unused")
val Float.dp: Float get() = this * getSystem().displayMetrics.density

/**
 * 是否支持SurfaceView缩放.
 */
val supportSurfaceViewScale = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N