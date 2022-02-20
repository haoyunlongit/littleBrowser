package com.smart.browser.little.ui.web

import android.graphics.Bitmap
import android.net.Uri
import android.os.Message
import android.view.View
import android.webkit.*
import androidx.annotation.CallSuper
import com.art.maker.util.Log

/**
 * 处理App网页的WebChromeClient.
 *
 * @author yushaojian
 * @date 2021-07-11 16:19
 */
open class AppWebChromeClient : WebChromeClient() {

    @CallSuper
    override fun onProgressChanged(view: WebView, newProgress: Int) {
        Log.d("AppWebChromeClient", "onProgressChanged newProgress $newProgress")
    }

    override fun onReceivedIcon(view: WebView, icon: Bitmap) {
        Log.d("AppWebChromeClient", "onReceivedIcon size ${icon.width}*${icon.height}")
    }

    override fun onReceivedTitle(view: WebView, title: String?) {
        Log.d("AppWebChromeClient", "onReceivedTitle $title")
    }

    override fun onPermissionRequest(request: PermissionRequest) {
        val host = request.origin.host ?: ""
        val requiredResources = request.resources
        val requiredPermissions = request.requiredPermissions()
        Log.w("AppWebChromeClient", "onPermissionRequest host $host")
        Log.d("AppWebChromeClient", "onPermissionRequest requiredResources $requiredResources")
        Log.d("AppWebChromeClient", "onPermissionRequest requiredPermissions $requiredPermissions")
        super.onPermissionRequest(request)
    }

    override fun onGeolocationPermissionsShowPrompt(origin: String, callback: GeolocationPermissions.Callback) {
        Log.w("AppWebChromeClient", "onGeolocationPermissionsShowPrompt $origin")
    }

    override fun onCreateWindow(view: WebView, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message): Boolean {
        Log.d("AppWebChromeClient", "onCreateWindow isDialog $isDialog isUserGesture $isUserGesture resultMsg $resultMsg")
        return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
    }

    override fun onCloseWindow(window: WebView) {
        Log.d("AppWebChromeClient", "onCloseWindow")
        super.onCloseWindow(window)
    }

    override fun onShowFileChooser(
        webView: WebView, filePathCallback: ValueCallback<Array<Uri>>,
        fileChooserParams: FileChooserParams
    ): Boolean {
        Log.w("AppWebChromeClient", "onShowFileChooser")
        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
    }

    override fun getDefaultVideoPoster(): Bitmap? {
        Log.d("AppWebChromeClient", "getDefaultVideoPoster")
        return super.getDefaultVideoPoster()
    }

    override fun getVideoLoadingProgressView(): View? {
        Log.d("AppWebChromeClient", "getVideoLoadingProgressView")
        return super.getVideoLoadingProgressView()
    }

    override fun onHideCustomView() {
        Log.d("AppWebChromeClient", "onHideCustomView")
        super.onHideCustomView()
    }

    override fun onShowCustomView(view: View, callback: CustomViewCallback) {
        Log.d("AppWebChromeClient", "onShowCustomView")
        super.onShowCustomView(view, callback)
    }

}