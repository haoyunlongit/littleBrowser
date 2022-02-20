package com.smart.browser.little.ui.news.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.smart.browser.little.BrowserApplication
import com.smart.browser.little.R
import com.smart.browser.little.databinding.FragmentHomeFeedBinding
import com.smart.browser.little.ui.getViewModelFactory
import com.smart.browser.little.ui.news.FeedAdapter
import com.smart.browser.little.ui.news.FeedNewsFragment
import com.smart.browser.little.ui.news.view.HomeFeedTitleView
import com.smart.browser.little.ui.news.vm.HomeFeedViewModel
import com.smart.browser.little.util.autoCleared
import com.art.maker.util.Log
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator

class HomeFeedFragment: Fragment() {

    companion object{
        const val TAG = "HomeFeedFragment"
    }

    private val viewModel by viewModels<HomeFeedViewModel> { getViewModelFactory() }
    private var binding by autoCleared<FragmentHomeFeedBinding>()
    private var sectionAdapter by autoCleared<FeedAdapter>()

    private var mTitlePairList = arrayListOf<Pair<String, String>>(
        Pair(BrowserApplication.context.getString(R.string.feed_top_news), "Top News"),
        Pair(BrowserApplication.context.getString(R.string.feed_weather), "Weather"),
        Pair(BrowserApplication.context.getString(R.string.feed_entertainment), "Entertainment"),
        Pair(BrowserApplication.context.getString(R.string.feed_sports), "Sports"),
        Pair(BrowserApplication.context.getString(R.string.feed_finance), "Finance"),
        Pair(BrowserApplication.context.getString(R.string.feed_lifestyle), "Lifestyle"),
        Pair(BrowserApplication.context.getString(R.string.feed_health), "Health"),
        Pair(BrowserApplication.context.getString(R.string.feed_dining), "Dining"),
        Pair(BrowserApplication.context.getString(R.string.feed_travel), "Travel"),
        Pair(BrowserApplication.context.getString(R.string.feed_autos), "Autos"),
        Pair(BrowserApplication.context.getString(R.string.feed_video), "Video"),
    )

    private val mFragments: ArrayList<Fragment>  by lazy {
        val list = ArrayList<Fragment>()
        mTitlePairList.forEach {
            list.add(FeedNewsFragment.newInstance(it.second))
        }
        list
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView(){
        initViewPager()
    }

    private fun initData(){

    }

    private fun initViewPager(){
        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int) = mFragments[position]
            override fun getItemCount() = mFragments.size
        }

        val commonNavigator = CommonNavigator(requireContext())
        commonNavigator.isAdjustMode = false
        commonNavigator.adapter = object : CommonNavigatorAdapter() {

            override fun getCount(): Int {
                return mTitlePairList.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val homeFeedTitleView = HomeFeedTitleView(requireContext()).apply {
                    setTitle(mTitlePairList[index].first)
                    setOnClickListener {
                        binding.viewPager.setCurrentItem(index, true)

                    }
                }
                return homeFeedTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                return LinePagerIndicator(context).apply {
                    mode = LinePagerIndicator.MODE_MATCH_EDGE
                    //线条的宽高度
                    lineHeight = UIUtil.dip2px(requireContext(), 3.0).toFloat()
                    //线条的圆角
                    roundRadius = UIUtil.dip2px(requireContext(), 1.5).toFloat()
                    startInterpolator = AccelerateInterpolator()
                    endInterpolator = DecelerateInterpolator()
                    //线条的颜色
                    setColors(0xFF2CA6A4.toInt())
                }
            }
        }

        binding.magicIndicator.navigator = commonNavigator

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.magicIndicator.onPageSelected(position)
                Log.d(TAG, "onPageSelected: $position")
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                binding.magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels)

            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                binding.magicIndicator.onPageScrollStateChanged(state)
                Log.d(TAG, "onPageScrollStateChanged: $state")

            }
        })
    }

}