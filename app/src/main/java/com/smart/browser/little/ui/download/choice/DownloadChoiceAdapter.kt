package com.smart.browser.little.ui.download.choice

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.art.vd.model.Video

/**
 * 下载文件选项Adapter.
 *
 * @author yushaojian
 * @date 2021-12-19 11:56
 */
class DownloadChoiceAdapter(
    private val lifecycle: LifecycleOwner,
    private val viewModel: DownloadChoiceViewModel,
    private val urls: Array<Video>
) :
    RecyclerView.Adapter<DownloadChoiceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DownloadChoiceViewHolder.create(parent)

    override fun onBindViewHolder(holder: DownloadChoiceViewHolder, position: Int) {
        holder.bind(lifecycle, viewModel, urls[position])
    }

    override fun getItemCount() = urls.size
}