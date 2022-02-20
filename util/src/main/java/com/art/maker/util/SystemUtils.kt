@file:Suppress("unused")

package com.art.maker.util

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.text.TextUtils
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

fun launchPlayStoreWithPackageName(context: Context, packageName: String) : Boolean {
    val uriString = "http://play.google.com/store/apps/details?id=$packageName"
    return launchPlayStoreWithUri(context, uriString)
}

fun launchPlayStoreWithUri(context: Context, uriString: String?) : Boolean {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(uriString)
    intent.setPackage("com.android.vending")
    if (context !is Activity) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    return try {
        context.startActivity(intent)
        true
    } catch (e: ActivityNotFoundException) {
        false
    }
}

fun openAppSettings(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
            Uri.fromParts(
                "package",
                context.packageName,
                null
            )
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        // oops
    }
}

fun open(context: Context, url: String?) {
    try {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse(url)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// 应用是否已安装
fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
    return try {
        val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
        applicationInfo.enabled
    } catch (e: Exception) {
        false
    }
}

fun composeEmail(context: Context, email: String, subject: String?) : Boolean {
    return try {
        val uri = Uri.parse("mailto:$email")
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.data = uri
        intent.putExtra(Intent.EXTRA_SUBJECT, subject) // 主题
        intent.putExtra(Intent.EXTRA_TEXT, "") // 正文
        context.startActivity(intent)
        true
    } catch (e: Exception) {
        false
    }
}

fun getProcessName(context: Context): String {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        return Application.getProcessName()
    }

    val cmdFile = File("/proc/self/cmdline")
    if (cmdFile.exists() && !cmdFile.isDirectory) {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(InputStreamReader(FileInputStream(cmdFile)))
            val procName = reader.readLine()
            if (!TextUtils.isEmpty(procName)) return procName.trim { it <= ' ' }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            try {
                reader?.close()
            } catch (e: Exception) {
            }
        }
    }

    val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val runningApps = am.runningAppProcesses
    if (runningApps != null) {
        val myPid = Process.myPid()
        for (procInfo in runningApps) {
            if (procInfo.pid == myPid) {
                return procInfo.processName
            }
        }
    }

    return context.applicationInfo.processName
}
