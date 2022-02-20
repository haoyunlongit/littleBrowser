package com.art.maker.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * 判断是否所有权限都已授权.
 */
fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
    return permissions.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }
}

fun Activity.somePermissionPermanentlyDenied(permissions: Array<String>): Boolean {
    return permissions.any { permissionPermanentlyDenied(it) }
}

fun Activity.permissionPermanentlyDenied(permission: String): Boolean {
    return !ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
}

fun Fragment.somePermissionPermanentlyDenied(permissions: Array<String>): Boolean {
    return permissions.any { permissionPermanentlyDenied(it) }
}

fun Fragment.permissionPermanentlyDenied(permission: String): Boolean {
    return !shouldShowRequestPermissionRationale(permission)
}
