package com.smart.browser.little.ui.web

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.smart.browser.little.EventObserver
import com.smart.browser.little.R
import com.smart.browser.little.ad.SiteExitAd
import com.smart.browser.little.databinding.WebNormalFragmentBinding
import com.smart.browser.little.ui.dialog.ExitWebAlertDialogFragment
import com.smart.browser.little.ui.getViewModelFactory
import com.smart.browser.little.ui.rate.RatingGuidePopupView
import com.smart.browser.little.ui.requestOrientationPortrait
import com.smart.browser.little.ui.requestOrientationUnspecified
import com.smart.browser.little.util.autoCleared

/**
 * 打开网页的页面.
 *
 * @author yushaojian
 * @date 2021-05-23 06:55
 */
class GameViewFragment : Fragment() {

    private var viewBinding by autoCleared<WebNormalFragmentBinding>()
    private val viewModel by viewModels<WebViewModel> { getViewModelFactory() }

    private val args by navArgs<GameViewFragmentArgs>()

    private var favoriteMenuItem: MenuItem? = null

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (viewBinding.webView.canGoBack()) {
                viewBinding.webView.goBack()
            } else {
                findNavController().navigate(
                    GameViewFragmentDirections.toExitWebAlertDialog()
                    //GameViewFragmentDirections.toAlertDialog(getString(R.string.alert), getString(R.string.exit_web_alert))
                )
            }
        }
    }

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
            allowFileAccess = true
            allowContentAccess = true
            setSupportZoom(false)
            displayZoomControls = false
            setGeolocationEnabled(true)
            domStorageEnabled = true
            this.setAppCacheEnabled(true)
            setRenderPriority(WebSettings.RenderPriority.HIGH)
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
        //DirectWebBannerWithAnimatorAd.showAd(requireActivity(), lifecycle, viewBinding.flBannerContainer)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.web_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        favoriteMenuItem = menu.findItem(R.id.item_favorite)
        val isFavorite = viewModel.isFavorite.value ?: false
        favoriteMenuItem?.setIcon(if (isFavorite) R.drawable.ic_favorite_black_24dp else R.drawable.ic_favorite_border_24dp)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (viewBinding.webView.canGoBack()) {
                viewBinding.webView.goBack()
            } else {
                findNavController().navigate(
                    GameViewFragmentDirections.toExitWebAlertDialog()
                    //GameViewFragmentDirections.toAlertDialog(getString(R.string.alert), getString(R.string.exit_web_alert))
                )
            }
            return true
        }
        if (item.itemId == R.id.item_favorite) {
            viewModel.toggleFavorite(args.site)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        SiteExitAd.load(requireActivity())
    }

    override fun onDestroy() {
        super.onDestroy()
        requestOrientationPortrait()
    }
}