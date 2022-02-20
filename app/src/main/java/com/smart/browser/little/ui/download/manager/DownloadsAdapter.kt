package com.smart.browser.little.ui.download.manager

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.art.vd.model.Video

class DownloadsAdapter(
    private val lifecycle: LifecycleOwner,
    private val viewModel: DownloadsViewModel
) :
    RecyclerView.Adapter<DownloadsViewHolder>() {

    private val videos = ArrayList<Video>()

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<Video>) {
        videos.clear()
        videos.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DownloadsViewHolder.create(parent)

    override fun onBindViewHolder(holder: DownloadsViewHolder, position: Int) {
        holder.bind(lifecycle, viewModel, videos[position])
    }

    override fun getItemCount() = videos.size
}