package com.smart.browser.little.ui.web

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.smart.browser.little.databinding.WebFragmentBinding
import com.smart.browser.little.util.autoCleared

/**
 * 打开网页的页面.
 *
 * @author yushaojian
 * @date 2021-05-23 06:55
 */
class WebViewFragment : Fragment() {

    private var viewBinding by autoCleared<WebFragmentBinding>()
    private val args by navArgs<WebViewFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewBinding = WebFragmentBinding.inflate(inflater, container, false)
        val webView = viewBinding.webView
        initWebView(webView)
        return viewBinding.root
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetJavaScriptEnabled", "SdCardPath")
    private fun initWebView(webView: WebView) {
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                if (!isAdded) return

                with(viewBinding.progressIndicator) {
                    visibility = if (newProgress < 100) View.VISIBLE else View.GONE
                    progress = newProgress
                }
            }
        }

        webView.webViewClient = WebViewClient() // 在 WebView 内打开链接 (当用户在 WebView 中点击网页中的链接时，Android 的默认行为是启动处理网址的应用)

        webView.settings.apply {
            javaScriptEnabled = true // 某些网站需要打开js
            mixedContentMode = 1
            allowContentAccess = false
            allowFileAccess = false
            allowFileAccessFromFileURLs = false
            allowUniversalAccessFromFileURLs = false
            setAppCacheEnabled(false)
        }
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.webView.loadUrl(args.url)
    }

}