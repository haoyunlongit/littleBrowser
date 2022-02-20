package com.smart.browser.little.ui.download.choice

import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.smart.browser.little.databinding.DownloadChoiceItemBinding
import com.art.vd.model.Video
import com.bumptech.glide.Glide

/**
 * 下载文件选项ViewHolder.
 *
 * @author yushaojian
 * @date 2021-12-19 11:53
 */
class DownloadChoiceViewHolder(private val binding: DownloadChoiceItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(lifecycle: LifecycleOwner, viewModel: DownloadChoiceViewModel, video: Video) {
        binding.apply {
            lifecycleOwner = lifecycle
            viewmodel = viewModel
            this.video = video
            executePendingBindings()
        }

        val preview = video.previewUrl ?: video.downloadUrl
        Glide.with(itemView.context)
            .load(preview)
            .placeholder(com.art.vd.R.drawable.video_preview_placeholder)
            .into(binding.thumbIV)

        binding.qualityTV.text = video.quality

        if (video.size > 0) {
            binding.sizeTV.text = Formatter.formatFileSize(itemView.context, video.size)
            binding.sizeTV.isVisible = true
        } else {
            binding.sizeTV.isVisible = false
        }
    }

    companion object {
        fun create(parent: ViewGroup): DownloadChoiceViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = DownloadChoiceItemBinding.inflate(layoutInflater, parent, false)
            return DownloadChoiceViewHolder(binding)
        }
    }
}