package com.smart.browser.little.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.ActivityInfo
import androidx.fragment.app.Fragment
import com.smart.browser.little.data.ServiceLocator

/**
 * Fragment拓展函数.
 *
 * @author yushaojian
 * @date 2021-05-27 09:28
 */

fun Fragment.getViewModelFactory(): ViewModelFactory {
    val context = requireContext().applicationContext
    val appsRepository = ServiceLocator.provideAppsRepository(context)
    return ViewModelFactory(appsRepository, context as Application)
}

@SuppressLint("SourceLockedOrientationActivity")
fun Fragment.requestOrientationPortrait() {
    val activity = activity ?: return
    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
}

@SuppressLint("SourceLockedOrientationActivity")
fun Fragment.requestOrientationUnspecified() {
    val activity = activity ?: return
    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
}