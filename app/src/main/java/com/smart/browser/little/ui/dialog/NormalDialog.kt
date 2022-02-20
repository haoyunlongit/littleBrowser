package com.smart.browser.little.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.art.maker.util.screenWidth

/**
 * @author shijingxing
 * @date 2021/10/18
 */
class NormalDialog(context: Context,
                   val windowPadding: Int = 0,
                   val gravity: Int = Gravity.CENTER,
                   val dimAmount: Float = 0.5F) :
        Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDialog()
    }

    fun initDialog(){
        window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.setLayout(
                screenWidth - windowPadding,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            )
            it.setGravity(gravity)
            it.setDimAmount(dimAmount)
        }
    }
}