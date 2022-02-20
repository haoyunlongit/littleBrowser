package com.smart.browser.little.manager

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import java.lang.Exception

fun sendFeedback(context: Context){
    try {
        val reciver = arrayOf("alex.lee20201@gmail.com")
        val myIntent = Intent(Intent.ACTION_SEND)
        myIntent.type = "plain/text"
        myIntent.putExtra(Intent.EXTRA_EMAIL, reciver)
        context.startActivity(Intent.createChooser(myIntent, ""))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun jumpGPMarket(activity: Activity) {
    var appPackageName: String? = null
    try {
        appPackageName = activity.packageName
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
        intent.setPackage("com.android.vending")
        activity.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
        )
        activity.startActivity(intent)
    }
}