package com.smart.browser.little.ui.rate

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import com.smart.browser.little.R

object PopupDisplayerUtils {
    /**
     * 弹窗是否在显示
     *
     * @param scenePopup
     * @return
     */
    fun isScenePopupShowing(scenePopup: ScenePopup?): Boolean {
        return scenePopup != null && scenePopup.isShowing
    }

    /**
     * 取消弹窗
     *
     * @param scenePopup
     */
    fun dismissScenePopup(scenePopup: ScenePopup?) {
        if (scenePopup != null && scenePopup.isShowing) {
            scenePopup.dismissPopup()
        }
    }

    /**
     * 创建全屏弹窗
     *
     * @param activity
     * @param contentView
     * @return
     */
    fun newFullScreenPopup(activity: Activity?, contentView: View?): FullScreenPopup {
        return FullScreenPopup(activity, contentView)
    }

    /**
     * 创建弹窗
     *
     * @param activity
     * @param contentView
     * @return
     */
    fun newNormalPopup(activity: Activity?, contentView: View?): NormalPopup {
        return newNormalPopup(activity, contentView, Gravity.CENTER)
    }

    /**
     * 创建弹窗
     *
     * @param activity
     * @param contentView
     * @return
     */
    fun newNormalPopup(activity: Activity?, contentView: View?, gravity: Int): NormalPopup {
        return newNormalPopup(activity, contentView, gravity, 0.5f)
    }

    /**
     * 创建弹窗
     *
     * @param activity
     * @param contentView
     * @return
     */
    fun newNormalPopup(
        activity: Activity?,
        contentView: View?,
        gravity: Int,
        dimAmount: Float
    ): NormalPopup {
        return NormalPopup(activity, contentView, gravity, dimAmount)
    }

    abstract class ScenePopup(context: Context?, protected var mContentView: View?) :
        Dialog(context!!, R.style.ScenePopupDialog) {
        /** BACK键监听  */
        interface OnDialogBackKeyListener {
            /**
             * BACK键处理
             * @return
             */
            fun onBackKey(): Boolean
        }

        protected var mOnDialogBackKeyListener: OnDialogBackKeyListener? = null
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(mContentView!!)
            initDialog()
            setOnKeyListener { dialog, keyCode, event ->
                if (KeyEvent.KEYCODE_BACK == keyCode && mOnDialogBackKeyListener != null) {
                    mOnDialogBackKeyListener!!.onBackKey()
                } else false
            }
        }

        /**
         *
         * @param onDialogBackKeyListener
         */
        fun setOnDialogBackKeyListener(onDialogBackKeyListener: OnDialogBackKeyListener?) {
            mOnDialogBackKeyListener = onDialogBackKeyListener
        }

        /**
         * 初始化弹窗
         */
        protected abstract fun initDialog()
        fun showPopup() {
            show()
        }

        fun dismissPopup() {
            dismiss()
        }

        fun setDimAmount(dimAmount: Float) {
            if (window != null) {
                window!!.setDimAmount(dimAmount)
            }
        }
    }

    /**
     * 全屏Dialog
     *
     * @param context
     * @param contentView
     */
    class FullScreenPopup(context: Context?, contentView: View?) :
        ScenePopup(context, contentView) {
        public override fun initDialog() {
            if (window != null) {
                window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                window!!.setDimAmount(0f)
            }
        }

        override fun show() {
            super.show()
        }
    }

    class NormalPopup : ScenePopup {
        private var mGravity = Gravity.CENTER
        private var mDimAmount = 0.5f

        constructor(context: Context?, contentView: View?) : super(context, contentView) {}
        constructor(context: Context?, contentView: View?, gravity: Int) : super(
            context,
            contentView
        ) {
            mGravity = gravity
        }

        constructor(context: Context?, contentView: View?, gravity: Int, dimAmount: Float) : super(
            context,
            contentView
        ) {
            mGravity = gravity
            mDimAmount = dimAmount
        }

        public override fun initDialog() {
            if (window != null) {
                window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                window!!.setGravity(mGravity)
                window!!.setDimAmount(mDimAmount)
            }
        }

        override fun show() {
            super.show()
        }
    }
}