package com.smart.browser.little.ui.news

import android.app.Activity
import android.view.View
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.smart.browser.little.R
import com.smart.browser.little.ui.web.AppViewFragmentDirections
import com.art.maker.data.model.NewsArticle
import com.art.maker.data.model.Site

/**
 * 作者　:  hl
 * 时间　:  2021/12/29
 * 描述　:
 **/

open abstract class BaseFeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var article: NewsArticle? = null

    init {
        itemView.setOnClickListener {
            Navigation.findNavController(itemView.context as Activity, R.id.nav_host_fragment)
                .navigate(
                    AppViewFragmentDirections.toAppViewFragment(
                        article?.title ?: "",
                        Site(
                            article?.title ?: "",
                            article?.thumbnailURL ?: "",
                            article?.clickURL ?: ""
                        )
                    )
                )
        }
    }

    abstract fun bind(section: NewsArticle)
}