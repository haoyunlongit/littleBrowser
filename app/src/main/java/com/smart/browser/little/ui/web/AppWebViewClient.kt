package com.smart.browser.little.ui.web

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Message
import android.webkit.*
import com.art.maker.util.Log
import com.art.vd.engine.VideoUrlExtractClient

/**
 * 处理App网页的WebViewClient.
 *
 * @author yushaojian
 * @date 2021-07-11 11:46
 */
open class AppWebViewClient : VideoUrlExtractClient() {

    override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
        Log.i("AppWebViewClient", "shouldInterceptRequest ${request.url}")
        return super.shouldInterceptRequest(view, request)
    }

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        Log.i("AppWebViewClient", "shouldOverrideUrlLoading ${request.url}")
        return super.shouldOverrideUrlLoading(view, request)
    }

    override fun onPageFinished(view: WebView, url: String) {
        val title = view.title
        Log.i("AppWebViewClient", "onPageFinished $url title $title")
    }

    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
        Log.i("AppWebViewClient", "onPageStarted $url")
    }

    override fun onReceivedHttpAuthRequest(view: WebView, handler: HttpAuthHandler, host: String, realm: String) {
        Log.i("AppWebViewClient", "onReceivedHttpAuthRequest host $host realm $realm")
        super.onReceivedHttpAuthRequest(view, handler, host, realm)
    }

    override fun onScaleChanged(view: WebView, oldScale: Float, newScale: Float) {
        Log.i("AppWebViewClient", "onScaleChanged newScale $newScale")
    }

    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        Log.i("AppWebViewClient", "onReceivedSslError $error")
        super.onReceivedSslError(view, handler, error)
    }

    override fun onFormResubmission(view: WebView, dontResend: Message, resend: Message) {
        Log.i("AppWebViewClient", "onFormResubmission")
        super.onFormResubmission(view, dontResend, resend)
    }
}