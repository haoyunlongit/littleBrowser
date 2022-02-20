package com.smart.browser.little.ui.exitapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.smart.browser.little.EventObserver
import com.smart.browser.little.ad.ExitAppAd
import com.smart.browser.little.databinding.ExitAppDialogBinding
import com.smart.browser.little.util.autoCleared
import com.art.maker.ad.AdListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * 退出app确认弹窗.
 *
 * @author yushaojian
 * @date 2021-06-16 21:17
 */
class ExitAppDialog : BottomSheetDialogFragment() {

    private var binding by autoCleared<ExitAppDialogBinding>()
    private val viewModel by viewModels<ExitAppViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ExitAppDialogBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.exitAppEvent.observe(viewLifecycleOwner, EventObserver {
            requireActivity().finish()
        })
    }

    override fun onResume() {
        super.onResume()
        addNativeAd()
    }

    private fun addNativeAd() {
        val viewGroup = binding.adLayout
        if (viewGroup.childCount > 0) {
            return // 已经添加了广告，不刷新
        }

        if (ExitAppAd.hasCache()) {
            ExitAppAd.show(viewLifecycleOwner.lifecycle, viewGroup)
        } else {
            binding.loadingLayout.isVisible = true
            ExitAppAd.addAdListener(object : AdListener() {
                override fun onAdLoaded(oid: String) {
                    if (!isAdded) return
                    binding.loadingLayout.isVisible = false
                    ExitAppAd.show(viewLifecycleOwner.lifecycle, viewGroup)
                }
            })
            ExitAppAd.load(requireActivity())
        }
    }

    override fun onDestroyView() {
        ExitAppAd.removeAdListeners()
        super.onDestroyView()
    }

}