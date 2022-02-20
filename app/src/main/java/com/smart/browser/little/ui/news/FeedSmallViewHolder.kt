package com.smart.browser.little.ui.news

import android.view.LayoutInflater
import android.view.ViewGroup
import com.smart.browser.little.databinding.FeedItemSmallBinding
import com.smart.browser.little.util.DensityUtil
import com.art.maker.data.model.NewsArticle
import com.bumptech.glide.Glide

/**
 * 分类网站的ViewHolder.
 *
 * @author yushaojian
 * @date 2021-06-17 21:49
 */
class FeedSmallViewHolder(
    private val binding: FeedItemSmallBinding,
) : BaseFeedViewHolder(binding.root) {

    private val width: Int = DensityUtil.dp2px(50f)
    private val height: Int = width

    override fun bind(section: NewsArticle) {
        article = section
        binding.titleTV.text = section.title
        Glide.with(itemView.context).load(section.thumbnailURL)
            .override(width, height)
            .placeholder(com.smart.browser.little.R.drawable.sharp_feed_placeholder)
            .into(binding.ivImageIcon)
        binding.ivImageIcon.layoutParams.height = height
        binding.ivImageIcon.layoutParams.width = width
    }

    companion object {
        fun from(
            parent: ViewGroup,
        ): FeedSmallViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = FeedItemSmallBinding.inflate(layoutInflater, parent, false)
            return FeedSmallViewHolder(binding)
        }
    }
}