package com.smart.browser.little.ui.web

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.webkit.ValueCallback
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.smart.browser.little.EventObserver
import com.smart.browser.little.R
import com.smart.browser.little.ad.SiteExitAd
import com.smart.browser.little.databinding.WebNormalFragmentBinding
import com.smart.browser.little.report.reportEvent
import com.smart.browser.little.ui.dialog.ExitWebAlertDialogFragment
import com.smart.browser.little.ui.download.manager.DownloadsActivity
import com.smart.browser.little.ui.download.manager.DownloadsManager
import com.smart.browser.little.ui.download.manager.isVideoDownloadABEnabled
import com.smart.browser.little.ui.getViewModelFactory
import com.smart.browser.little.ui.rate.RatingGuidePopupView
import com.smart.browser.little.ui.requestOrientationPortrait
import com.smart.browser.little.ui.requestOrientationUnspecified
import com.smart.browser.little.util.autoCleared
import com.art.maker.util.isYoutube
import com.art.vd.model.Video

/**
 * 打开App网页的页面.
 *
 * @author yushaojian
 * @date 2021-05-23 06:55
 */
class AppViewFragment : Fragment(), DownloadsManager.DownloadCallback {

    private var viewBinding by autoCleared<WebNormalFragmentBinding>()
    private val viewModel by viewModels<WebViewModel> { getViewModelFactory() }

    private val args by navArgs<AppViewFragmentArgs>()

    private var favoriteMenuItem: MenuItem? = null

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (viewBinding.webView.canGoBack()) {
                viewBinding.webView.goBack()
            } else {
                findNavController().navigate(
                    AppViewFragmentDirections.toExitWebAlertDialog()
                    //AppViewFragmentDirections.toAlertDialog(getString(R.string.alert), getString(R.string.exit_web_alert))
                )
            }
        }
    }

    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        val results: Array<Uri>? = if (uri != null) arrayOf(uri) else null
        filePathCallback?.onReceiveValue(results)
    }

    private var extractedVideoUrls: List<Video>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestOrientationUnspecified()
        setFragmentResultListener(ExitWebAlertDialogFragment.ALERT_DIALOG_REQUEST) { _, bundle ->
            val accepted = bundle.getBoolean(ExitWebAlertDialogFragment.ALERT_DIALOG_RESULT, false)
            if (accepted) {
                val requireActivity = requireActivity()
                findNavController().popBackStack()
                val showed = SiteExitAd.show(requireActivity){
                    RatingGuidePopupView.openRatingGuidePopupView(requireActivity)
                }
                if(!showed){
                    RatingGuidePopupView.openRatingGuidePopupView(requireActivity)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewBinding = WebNormalFragmentBinding.inflate(inflater, container, false)
        viewModel.setApp(args.site)
        val webView = viewBinding.webView
        initWebView(webView)
        setHasOptionsMenu(true)
        return viewBinding.root
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetJavaScriptEnabled", "SdCardPath")
    private fun initWebView(webView: WebView) {
        webView.webChromeClient = object : AppWebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                if (!isAdded) return

                super.onProgressChanged(view, newProgress)
                with(viewBinding.progressIndicator) {
                    visibility = if (newProgress < 100) View.VISIBLE else View.GONE
                    progress = newProgress
                }
            }

            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                if (!isAdded) return false

                super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
                this@AppViewFragment.filePathCallback = filePathCallback
                getContent.launch("image/*")
                return true
            }
        }

        // 在 WebView 内打开链接 (当用户在 WebView 中点击网页中的链接时，Android 的默认行为是启动处理网址的应用)
        webView.webViewClient = object : AppWebViewClient() {
            override fun onLoadResource(view: WebView, url: String) {
                if (isAdded && isVideoDownloadABEnabled() && !isYoutube(view.url) && !isYoutube(url)) {
                    super.onLoadResource(view, url)
                }
            }

            override fun onVideoUrlExtracted(extracted: List<Video>) {
                if(!lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)){
                    return
                }
                extractedVideoUrls = extracted
                viewBinding.downloadIV.visibility = View.VISIBLE
                if(!viewModel.hasUploadDownloadBtn){
                    viewModel.hasUploadDownloadBtn = true
                    reportEvent("video_download_float_btn_show")
                }
            }
        }

        webView.settings.apply {
            javaScriptEnabled = true // 某些网站需要打开js
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            domStorageEnabled = true
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
        viewBinding.webView.loadUrl(args.site.url)
        viewModel.isFavorite.observe(viewLifecycleOwner) {
            favoriteMenuItem?.setIcon(if (it) R.drawable.ic_favorite_black_24dp else R.drawable.ic_favorite_border_24dp)
        }
        viewModel.favoriteToggled.observe(viewLifecycleOwner, EventObserver {
            val message = if (it) R.string.added_to_favorites else R.string.removed_from_favorites
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        })

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedCallback)

        viewBinding.downloadIV.setOnClickListener {
            kotlin.runCatching {
                reportEvent("video_download_float_btn_click", extra = (Uri.parse(viewBinding.webView.url).host))
                toDownloadChoice()
            }
        }

        DownloadsManager.setDownloadCallback(this)

        //DirectWebBannerWithAnimatorAd.showAd(requireActivity(), lifecycle, viewBinding.flBannerContainer)
    }

    private fun toDownloadChoice() {
        val videos = extractedVideoUrls
        if (videos.isNullOrEmpty()) {
            return
        }

        val choices = videos.toTypedArray()
        findNavController().navigate(AppViewFragmentDirections.toDownloadChoiceDialog(choices))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val menuRes = if (isVideoDownloadABEnabled()) R.menu.app_menu else R.menu.web_menu
        inflater.inflate(menuRes, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        favoriteMenuItem = menu.findItem(R.id.item_favorite)
        val isFavorite = viewModel.isFavorite.value ?: false
        favoriteMenuItem?.setIcon(if (isFavorite) R.drawable.ic_favorite_black_24dp else R.drawable.ic_favorite_border_24dp)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (viewBinding.webView.canGoBack()) {
                    viewBinding.webView.goBack()
                } else {
                    findNavController().navigate(
                        AppViewFragmentDirections.toExitWebAlertDialog()
                        //AppViewFragmentDirections.toAlertDialog(getString(R.string.alert), getString(R.string.exit_web_alert))
                    )
                }
                return true
            }
            R.id.item_favorite -> {
                viewModel.toggleFavorite(args.site)
                return true
            }
            R.id.item_downloads -> {
                startActivity(Intent(requireActivity(), DownloadsActivity::class.java))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        SiteExitAd.load(requireActivity())
    }

    override fun onDestroy() {
        super.onDestroy()
        requestOrientationPortrait()
        DownloadsManager.setDownloadCallback(null)
    }

    override fun onDownloadStart(video: Video) {
        val title = video.title ?: getString(R.string.a_video)
        Toast.makeText(requireContext(), getString(R.string.download_start, title), Toast.LENGTH_SHORT).show()
    }

    override fun onDownloadCompleted(video: Video) {
        val title = video.title ?: getString(R.string.a_video)
        Toast.makeText(requireContext(), getString(R.string.download_completed, title), Toast.LENGTH_SHORT).show()
    }

    override fun onDownloadFailed(video: Video) {
        val title = video.title ?: getString(R.string.a_video)
        Toast.makeText(requireContext(), getString(R.string.download_failed, title), Toast.LENGTH_SHORT).show()
    }
}