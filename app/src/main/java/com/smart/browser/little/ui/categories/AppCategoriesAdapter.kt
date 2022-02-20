package com.smart.browser.little.ui.categories

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.smart.browser.little.R
import com.bumptech.glide.Glide

/**
 * App分类适配器.
 *
 * @author yushaojian
 * @date 2021-05-05 15:10
 */
open class AppCategoriesAdapter : RecyclerView.Adapter<AppCategoriesAdapter.CategoryViewHolder>() {

    private val categories: MutableList<StatefulCategory> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<StatefulCategory>) {
        this.categories.clear()
        this.categories.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_category, parent, false)
        val viewHolder = CategoryViewHolder(itemView)
        viewHolder.itemView.setOnClickListener { onCategoryClick(categories[viewHolder.bindingAdapterPosition]) }
        return viewHolder
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    protected open fun onCategoryClick(statefulCategory: StatefulCategory) {}

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val titleTV: TextView = itemView.findViewById(R.id.titleTV)
        private val iconIV: ImageView = itemView.findViewById(R.id.iconIV)
        private val unlockTV: View = itemView.findViewById(R.id.unlockTV)

        fun bind(statefulCategory: StatefulCategory) {
            val category = statefulCategory.category
            titleTV.text = category.name
            Glide.with(iconIV.context).load(category.icon).into(iconIV)
            unlockTV.isVisible = !statefulCategory.unlocked
        }
    }

}