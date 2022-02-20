package com.art.maker.data.model

import androidx.annotation.Keep

/**
 * 新闻sdk接口返回数据结构
 */
@Keep
data class News(
    /**
     * 请求状态
     */
    val statusCode: Int,
    /**
     * 请求状态描述
     */
    val statusDescription: String?,
    /**
     * 内容
     */
    val report: Report?,
)

@Keep
data class Report(
    val attribution: String?,
    val articles: List<NewsArticle>?,
    val sessionId: String?,
    val impressionURL: String?,
)

/**
 * 文章内容结构
 */
@Keep
data class NewsArticle(
    val id: String?,
    val promoted: Boolean,
    /**
     * 标题
     */
    val title: String?,
    /**
     * 摘要
     */
    val description: String?,
    /**
     * 链接
     */
    val impressionURL: String?,
    /**
     * 点击链接
     */
    val clickURL: String?,
    /**
     * 分享链接
     */
    val shareURL: String?,
    /**
     * 小图
     */
    val thumbnailURL: String?,
    /**
     * 中图
     */
    val largeThumbnailURL: String?,
    /**
     * 大图
     */
    val xlargeThumbnailURL: String?,
    /**
     * 发布时间
     */
    val publishTime: String?,
    /**
     * 作者
     */
    val author: String?,
    /**
     * 分类
     */
    val categories: List<String>?,
)