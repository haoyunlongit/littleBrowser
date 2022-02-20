package com.art.maker.view

import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout

/**
 * 内容区填满父控件（默认是wrap）的BottomSheetDialogFragment.
 *
 * @author yushaojian
 * @date 2021-05-30 18:58
 */
open class MatchParentBottomSheetDialogFragment : CollapsedBottomSheetFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sheet: View = view.parent as View
        val layoutParams = sheet.layoutParams
        layoutParams.height = CoordinatorLayout.LayoutParams.MATCH_PARENT
        sheet.layoutParams = layoutParams
    }

}