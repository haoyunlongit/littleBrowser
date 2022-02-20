package com.smart.browser.little.ui.rate

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.smart.browser.little.R

import kotlin.Unit

import android.app.Activity
import com.smart.browser.little.ui.rate.PopupDisplayerUtils.dismissScenePopup
import com.smart.browser.little.ui.rate.PopupDisplayerUtils.newNormalPopup
import com.art.maker.util.SPUtils


class RatingGuidePopupView : FrameLayout {
    companion object{
        fun openRatingGuidePopupView(activity: Activity) {
            val optimizeTime = increaseOptimizeTime()
            if (optimizeTime != 5) {
                return
            }
            val popupView = RatingGuidePopupView(activity)
            val popup = newNormalPopup(activity, popupView)
            popupView.setCloseAction {
                dismissScenePopup(popup)
            }
            popup.setCancelable(true)
            popup.setCanceledOnTouchOutside(false)
            popup.show()
        }
        fun increaseOptimizeTime():Int {
            var optimizerTime = SPUtils.getInt("function_use_time", 0)
            optimizerTime++
            SPUtils.putInt("function_use_time", optimizerTime)
            return optimizerTime
        }
    }

    private lateinit var mTvContent: TextView
    private lateinit var mTvFeedback: TextView
    private lateinit var mTvRate: TextView
    private lateinit var mIvBack: ImageView
    private var closeAction: (() -> Unit)? = null
    private var goAction: (() -> Unit)? = null

    fun setGoAction(goAction: () -> Unit) {
        this.goAction = goAction
    }

    fun setCloseAction(closeAction: () -> Unit) {
        this.closeAction = closeAction
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.rating_guide_view, this)
        mTvContent = findViewById<TextView>(R.id.tv_content)
        mTvFeedback = findViewById<TextView>(R.id.tv_feedback)
        mTvRate = findViewById<TextView>(R.id.tv_rate)
        mIvBack = findViewById<ImageView>(R.id.iv_back)
        val appName = context.getString(R.string.app_name)
        val rateText: String = context.getString(R.string.rating_text2, appName, appName)
        mTvContent.text = rateText
        /*if (rateText.contains("%s")) {
            mTvContent.text = String.format(rateText, context.getString(R.string.app_name))
        } else {
            mTvContent.text = rateText
        }*/

        mTvFeedback.setOnClickListener(OnClickListener {
            val uri: Uri = Uri.parse("mailto:3802**92@qq.com")
            val email = arrayOf<String>("shi.tony12@gamil.com")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            //intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, email)
            context.startActivity(Intent.createChooser(intent, "Select email application."))
            closeAction?.invoke()
        })

        mTvRate.setOnClickListener(OnClickListener {
            val appPackageName: String = context.packageName
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
                intent.setPackage("com.android.vending")
                context.startActivity(intent)
            } catch (anfe: ActivityNotFoundException) {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
            }
            closeAction?.invoke()
        })

        mIvBack.setOnClickListener{
            closeAction?.invoke()
        }
    }
}