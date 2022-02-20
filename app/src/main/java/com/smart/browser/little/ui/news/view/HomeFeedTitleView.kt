package com.smart.browser.little.ui.news.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.smart.browser.little.R
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IMeasurablePagerTitleView

class HomeFeedTitleView : FrameLayout, IMeasurablePagerTitleView {

    private lateinit var mTvTitle: TextView

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }


    private fun initView(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.home_feed_title_view, this)
        mTvTitle = findViewById(R.id.tv_title)

    }

    fun setTitle(title: String){
        mTvTitle.text = title
    }


    override fun getContentRight(): Int {
        return measuredWidth
    }

    override fun getContentLeft(): Int {
        return 0
    }

    override fun getContentBottom(): Int {
        return measuredHeight
    }

    override fun getContentTop(): Int {
        return 0
    }

    override fun onDeselected(index: Int, totalCount: Int) {
        mTvTitle.setTextColor(0xAA2CA6A4.toInt())
    }

    override fun onSelected(index: Int, totalCount: Int) {
        mTvTitle.setTextColor(0xFF2CA6A4.toInt())
    }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
    }


}