package com.smart.browser.little.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smart.browser.little.databinding.ItemPlaceholderBinding

/**
 * 信息流原生广告占位.
 *
 * @author yushaojian
 * @date 2021-06-23 16:49
 */
class PlaceholderViewHolder(val binding: ItemPlaceholderBinding) : RecyclerView.ViewHolder(binding.root){
    companion object {
        fun create(parent: ViewGroup): PlaceholderViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemPlaceholderBinding.inflate(layoutInflater, parent, false)
            return PlaceholderViewHolder(binding)
        }
    }
}