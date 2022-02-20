package com.art.maker.data.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * 设置RecyclerView的item间距，支持多view type的Adapter.
 *
 * 只支持GridLayoutManager.
 *
 * [horizontalSpacing] item间横向间距
 * [verticalSpacing] item间纵向间距
 * [includeHorizontalEdge] item横向边缘是否需要间距
 * [includeVerticalEdge] item纵向边缘是否需要间距
 *
 * @author yushaojian
 * @date 2021-01-21 10:10
 */
@Suppress("unused")
class SpacerItemDecoration(private val horizontalSpacing: Int, private val verticalSpacing: Int = horizontalSpacing,
                           private val includeHorizontalEdge: Boolean = true,
                           private val includeVerticalEdge: Boolean = true) : ItemDecoration() {

    private var spanCount = 0
    private var spanSizeLookup: GridLayoutManager.SpanSizeLookup? = null

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (spanCount == 0) {
            val layoutManager = parent.layoutManager as GridLayoutManager
            spanCount = layoutManager.spanCount
            spanSizeLookup = layoutManager.spanSizeLookup
        }

        val position = parent.getChildLayoutPosition(view)
        var effectivePosition = 0 // 等效位置，有些item可能横跨多列，这些item会对之后的行、列计算产生影响
        for (i in 0 until position) {
            effectivePosition += spanSizeLookup?.getSpanSize(i) ?: 1
        }
        val column = effectivePosition % spanCount
        val effectiveSpanCount = spanCount - (spanSizeLookup?.getSpanSize(position) ?: 1) + 1

        // 横向算法
        if (includeHorizontalEdge) {
            /*
                设x是某列，x+1是它的下一列
                如果x是第1列，则：
                x.left = horizontalSpacing
                如果x是最后一列，则
                x.right = horizontalSpacing
                x是中间列，则x和x+1的间距应满足：
                x.right + (x + 1).left = horizontalSpacing
                由此可总结出横向算法
            */

            outRect.left = horizontalSpacing - column * horizontalSpacing / effectiveSpanCount
            outRect.right = (column + 1) * horizontalSpacing / effectiveSpanCount
        } else {
            /*
                设x是某列，x+1是它的下一列
                如果x是第1列，则：
                x.left = 0
                如果x是最后一列，则
                x.right = 0
                x是中间列，则x和x+1的间距应满足：
                x.right + (x + 1).left = horizontalSpacing
                由此可总结出横向算法
            */

            outRect.left = column * horizontalSpacing / effectiveSpanCount
            outRect.right = horizontalSpacing - (column + 1) * horizontalSpacing / effectiveSpanCount
        }

        if (includeVerticalEdge) {
            if (effectivePosition < effectiveSpanCount) {
                outRect.top = verticalSpacing
            }
            outRect.bottom = verticalSpacing
        } else {
            if (effectivePosition >= effectiveSpanCount) {
                outRect.top = verticalSpacing
            }
        }
    }
}