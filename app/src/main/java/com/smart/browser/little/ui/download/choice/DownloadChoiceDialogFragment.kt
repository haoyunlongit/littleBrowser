package com.smart.browser.little.ui.download.choice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.smart.browser.little.EventObserver
import com.smart.browser.little.R
import com.smart.browser.little.ad.VideoDownloadRewardAd
import com.smart.browser.little.databinding.DownloadChoiceFragmentBinding
import com.smart.browser.little.report.reportEvent
import com.smart.browser.little.ui.base.BindingDialogFragment
import com.smart.browser.little.ui.download.manager.DownloadsManager
import com.art.maker.ad.AdListener
import com.art.vd.model.Video

/**
 * 下载文件选择页.
 *
 * @author yushaojian
 * @date 2021-12-19 11:42
 */
class DownloadChoiceDialogFragment : BindingDialogFragment<DownloadChoiceFragmentBinding>() {

    private val viewModel by viewModels<DownloadChoiceViewModel>()
    private var selectedVideos: List<Video>? = null
    private var needCloseWhenResume: Boolean = false

    private val adListener = object : AdListener() {

        private var rewardEarned = false

        override fun onRewardEarned(oid: String) {
            super.onRewardEarned(oid)
            rewardEarned = true
        }

        override fun onAdClosed(oid: String) {
            super.onAdClosed(oid)
            if (rewardEarned) {
                selectedVideos?.let { DownloadsManager.downloadVideos(it) }
                if(isResumed){
                    findNavController().popBackStack()
                }else{
                    needCloseWhenResume = true
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.AlertDialog)
    }
    override fun onResume() {
        super.onResume()
        if(needCloseWhenResume){
            findNavController().popBackStack()
        }
    }

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?) =
        DownloadChoiceFragmentBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = viewModel
        }

    override fun initViews() {
        val args by navArgs<DownloadChoiceDialogFragmentArgs>()
        binding.choicesRV.adapter = DownloadChoiceAdapter(viewLifecycleOwner, viewModel, args.choices)
    }

    override fun initObservers() {
        viewModel.toDownloadsEvent.observe(viewLifecycleOwner, EventObserver {
            selectedVideos = it
            //animToAlertView()
            selectedVideos?.let { DownloadsManager.downloadVideos(it) }
            findNavController().popBackStack()
            reportEvent("video_download_btn")
        })

        binding.tvCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvConfirm.setOnClickListener {
            val show = VideoDownloadRewardAd.show(requireActivity())
            if(!show){
                Toast.makeText(requireContext(), R.string.load_video_retry, Toast.LENGTH_SHORT).show()
            }
            VideoDownloadRewardAd.load(requireActivity())
            /*selectedVideos?.let { DownloadsManager.downloadVideos(it) }
            findNavController().popBackStack()*/
            reportEvent("video_download_confirm_btn", extra = show.toString())
        }

        //VideoDownloadRewardAd.addAdListener(adListener)
    }

    private fun animToAlertView() {
        binding.root.animate().alpha(0f).setDuration(100).withEndAction {
            if (!isAdded) return@withEndAction
            binding.contentLayout.visibility = View.GONE
            binding.alertLayout.visibility = View.VISIBLE
            binding.root.animate().alpha(1f).setDuration(100).start()
        }
    }

    override fun onDestroyView() {
        VideoDownloadRewardAd.removeAdListener(adListener)
        super.onDestroyView()
    }
}