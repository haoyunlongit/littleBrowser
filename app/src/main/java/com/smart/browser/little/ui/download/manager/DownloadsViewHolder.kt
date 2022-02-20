package com.smart.browser.little.ui.download.manager

import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.smart.browser.little.databinding.ItemDownloadBinding
import com.art.vd.model.Video
import com.bumptech.glide.Glide

class DownloadsViewHolder(private val binding: ItemDownloadBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(lifecycle: LifecycleOwner, viewModel: DownloadsViewModel, video: Video) {
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
            .into(binding.previewIV)

        binding.titleTV.text = video.title

        val isWebVideo = video.downloadUrl.startsWith("http")
        binding.downloadingPB.isVisible = isWebVideo

        if (video.size > 0) {
            binding.sizeTV.text = Formatter.formatFileSize(itemView.context, video.size)
            binding.sizeTV.isVisible = true
        } else {
            binding.sizeTV.isVisible = false
        }

        binding.rootLayout.isEnabled = !isWebVideo
    }

    companion object {
        fun create(parent: ViewGroup): DownloadsViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemDownloadBinding.inflate(layoutInflater, parent, false)
            return DownloadsViewHolder(binding)
        }
    }
}