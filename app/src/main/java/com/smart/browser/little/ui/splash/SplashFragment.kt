package com.smart.browser.little.ui.splash

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.smart.browser.little.EventObserver
import com.smart.browser.little.NavGraphDirections
import com.smart.browser.little.R
import com.smart.browser.little.ad.AppOpenAd
import com.smart.browser.little.ad.BannerInterAd
import com.smart.browser.little.ad.SplashInter
import com.smart.browser.little.ad.SplashNativeInter
import com.smart.browser.little.databinding.SplashFragmentBinding
import com.smart.browser.little.ui.getViewModelFactory
import com.smart.browser.little.util.autoCleared
import com.art.maker.ad.AdListener

/**
 * 启动页.
 *
 * @author yushaojian
 * @date 2021-06-06 06:09
 */
class SplashFragment : Fragment() {

    private var binding by autoCleared<SplashFragmentBinding>()
    private val viewModel by viewModels<SplashViewModel> { getViewModelFactory() }

    private var jumped = false // 用于保证只跳转一次. 在超时前请求到广告或超时后都会跳转.
    private val adListener = object : AdListener() {
        override fun onAdClosed(oid: String) {
            toHome()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.start(requireActivity())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        AppOpenAd.blockShow() // 防止温启动且有缓存时开屏广告被直接打开
        binding = SplashFragmentBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = viewModel
        }
        binding.privacyTV.movementMethod = LinkMovementMethod()
        binding.privacyTV.text = buildPrivacyPolicyContent(requireContext()) { context, url ->
            val directions = NavGraphDirections.toWebViewFragment(context.getString(R.string.privacy_policy), url)
            findNavController().navigate(directions)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.dataLoadCompletedEvent.observe(viewLifecycleOwner, EventObserver {
            val hasSplashAd =
                AppOpenAd.hasCache() || SplashNativeInter.hasCache() || BannerInterAd.hasCache() || SplashInter.hasCache()
            if (isAdded && isResumed && hasSplashAd) {
                var adShown = false
                when {
                    AppOpenAd.hasCache() -> {
                        AppOpenAd.addAdListener(adListener)
                        AppOpenAd.cancelBlockShow()
                        adShown = AppOpenAd.show(requireActivity())
                    }
                    SplashInter.hasCache() -> {
                        SplashInter.addAdListener(adListener)
                        adShown = SplashInter.show(requireActivity())
                    }
                    SplashNativeInter.hasCache() -> {
                        SplashNativeInter.addAdListener(adListener)
                        adShown = SplashNativeInter.show(requireActivity())
                    }
                    BannerInterAd.hasCache() -> {
                        BannerInterAd.addAdListener(adListener)
                        adShown = BannerInterAd.show(requireActivity())
                    }
                }
                if (!adShown) {
                    toHome()
                }
            } else {
                toHome()
            }
        })
        viewModel.privacyPolicyAcceptedEvent.observe(viewLifecycleOwner, EventObserver {
            viewModel.start(requireActivity())
        })
    }

    override fun onResume() {
        super.onResume()
        if (jumped && isAdded) {
            toHome() // findNavController().navigate有个bug，在有些手机上要resume后才能跳转
        }
    }

    private fun toHome() {
        jumped = true
        findNavController().navigate(SplashFragmentDirections.toHome())
    }

    override fun onDestroyView() {
        AppOpenAd.removeAdListener(adListener)
        AppOpenAd.cancelBlockShow()
        SplashInter.removeAdListener(adListener)
        SplashNativeInter.removeAdListener(adListener)
        BannerInterAd.removeAdListener(adListener)
        super.onDestroyView()
    }

}