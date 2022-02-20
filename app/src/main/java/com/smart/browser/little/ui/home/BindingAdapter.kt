package com.smart.browser.little.ui.home

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

@BindingAdapter("items")
fun setItems(recyclerView: RecyclerView, items: ArrayList<HomeItem>?) {
    items?.let {
        (recyclerView.adapter as HomeAdapter).submitList(items)
    }
}