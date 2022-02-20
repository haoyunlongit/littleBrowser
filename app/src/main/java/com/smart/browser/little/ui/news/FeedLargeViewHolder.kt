package com.smart.browser.little.ui.news

import android.view.LayoutInflater
import android.view.ViewGroup
import com.smart.browser.little.databinding.FeedItemLargeBinding
import com.smart.browser.little.util.DateUtils
import com.smart.browser.little.util.DensityUtil
import com.art.maker.data.model.NewsArticle
import com.bumptech.glide.Glide

/**
 * 分类网站的ViewHolder.
 *
 * @author yushaojian
 * @date 2021-06-17 21:49
 */
class FeedLargeViewHolder(
    private val binding: FeedItemLargeBinding,
) : BaseFeedViewHolder(binding.root) {
    val width: Int
    val height: Int

    init {
        val screenWidth = DensityUtil.getScreenWidth(itemView.context)
        width = screenWidth - DensityUtil.dp2px(32f)
        height = (width * 736.0f / 1244).toInt()
    }

    override fun bind(section: NewsArticle) {
        article = section
        binding.tvTitle.text = section.title ?: ""
        Glide.with(itemView.context).load(section.xlargeThumbnailURL)
            .override(width, height)
            .placeholder(com.smart.browser.little.R.drawable.sharp_feed_placeholder)
            .into(binding.ivImageIcon)
        binding.ivImageIcon.layoutParams.height = height
        binding.ivImageIcon.layoutParams.width = width
        binding.tvAuthor.text = section.author ?: ""
        binding.tvDate.text = DateUtils.formatDate(section.publishTime)
        binding.tvTime.text = DateUtils.formatTime(section.publishTime)

    }

    companion object {
        fun from(
            parent: ViewGroup,
        ): FeedLargeViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = FeedItemLargeBinding.inflate(layoutInflater, parent, false)
            return FeedLargeViewHolder(binding)
        }
    }
}