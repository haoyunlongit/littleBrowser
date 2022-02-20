package com.art.maker.view

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * 不收缩的BottomSheetDialogFragment.
 *
 * @author yushaojian
 * @date 2021-05-30 18:56
 */
open class CollapsedBottomSheetFragment : BottomSheetDialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            val sheet: View = view.parent as View
            val bottomSheetBehavior = BottomSheetBehavior.from(sheet)
            bottomSheetBehavior.peekHeight = Resources.getSystem().displayMetrics.heightPixels
        } catch (e: Exception) {
        }
    }

}