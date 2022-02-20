package com.smart.browser.little.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smart.browser.little.BrowserApplication
import com.smart.browser.little.R
import com.smart.browser.little.ad.HomeNewsAd
import com.smart.browser.little.ad.RecycleAdvertHelper
import com.smart.browser.little.databinding.FragmentFeedBinding
import com.smart.browser.little.report.PAGE_FEED
import com.smart.browser.little.report.reportPageShow
import com.smart.browser.little.ui.getViewModelFactory
import com.smart.browser.little.util.autoCleared
import com.art.maker.util.Log
import com.scwang.smart.refresh.footer.BallPulseFooter
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.coroutines.launch


/**
 * 信息流页.
 *
 * @author yushaojian
 * @date 2021-07-28 07:43
 */
class FeedNewsFragment : Fragment() {

    companion object {
        const val TAG = "FeedNewsFragment"
        fun newInstance(category: String): FeedNewsFragment {
            val fragment = FeedNewsFragment()
            val bundle = Bundle()
            bundle.putString("category", category)
            fragment.arguments = bundle
            return fragment
        }
    }

    private val viewModel by viewModels<FeedViewModel> { getViewModelFactory() }
    private var binding by autoCleared<FragmentFeedBinding>()
    private var sectionAdapter by autoCleared<FeedAdapter>()
    private val advertHelper = RecycleAdvertHelper()
    private var layoutManager: LinearLayoutManager? = null
    private var mCategory = DEFAULT_CATEGORY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mCategory = it.getString("category", DEFAULT_CATEGORY)
        }
        viewModel.mCategory = mCategory
        viewModel.refresh()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = viewModel
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    init {
        val color = ContextCompat.getColor(BrowserApplication.context, R.color.colorPrimary)
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
            BallPulseFooter(
                context
            ).setNormalColor(color)
                .setAnimatingColor(color)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager
        sectionAdapter = FeedAdapter(this)
        binding.recyclerView.adapter = sectionAdapter
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!isAdded || !isResumed) return
                updateAdByScroll(recyclerView, newState)
            }
        })

        val refreshLayout = binding.refreshLayout

        refreshLayout.setOnRefreshListener { refreshers ->
            viewModel.mainScope.launch {
                viewModel.refresh()
                refreshers.finishRefresh(400 /*,false*/) //传入false表示刷新失败
            }

        }
        refreshLayout.setOnLoadMoreListener { refreshers ->
            viewModel.mainScope.launch {
                viewModel.loadMore()
                refreshers.finishLoadMore(100 /*,false*/) //传入false表示加载失败
            }

        }
        viewModel.dataLoading.observe(this.viewLifecycleOwner){
            if(!it && isResumed){
                Log.d("reloadAdIfNull", "FeedNewsFragment reloadAdIfNull dataloading false start")
                reloadAdIfNull()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        HomeNewsAd.load(requireActivity())
        // 埋点
        reportPageShow(PAGE_FEED)
        if(viewModel.dataLoading.value == false){
            Log.d("reloadAdIfNull", "FeedNewsFragment reloadAdIfNull onResume start")
            reloadAdIfNull()
        }
        Log.d(TAG, "$mCategory onResume")
    }

    fun reloadAdIfNull(){
        advertHelper.reloadAdIfNull<PlaceholderItem, AdItem>(
            requireActivity(),
            this,
            viewModel.mainScope,
            layoutManager!!,
            { viewModel.homeItems.value },
            { HomeNewsAd },
            { HomeNewsAd.getAd() },
            { AdItem(it) }
        )
        { pos, adItem ->
            sectionAdapter.replace(pos, adItem)
        }
    }

    fun updateAdByScroll(recyclerView: RecyclerView, newState: Int) {
        advertHelper.reLoadAdByScroll<PlaceholderItem, AdItem>(requireActivity(),
            recyclerView,
            newState,
            sectionAdapter.itemCount,
            { sectionAdapter.getItem(it) },
            { HomeNewsAd.getAd() },
            { HomeNewsAd.load(it) },
            { AdItem(it) },
            { pos, ad -> sectionAdapter.replace(pos, ad) }
        )
    }
}