package com.smart.browser.little.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.smart.browser.little.NavGraphDirections
import com.smart.browser.little.R
import com.smart.browser.little.ad.SiteEnterAd
import com.smart.browser.little.databinding.SearchFragmentBinding
import com.smart.browser.little.report.PAGE_SEARCH
import com.smart.browser.little.report.reportAppClick
import com.smart.browser.little.report.reportPageShow
import com.smart.browser.little.ui.getViewModelFactory
import com.smart.browser.little.ui.viewSite
import com.smart.browser.little.util.autoCleared
import com.smart.browser.little.market.MarketEventHelper
import com.art.maker.view.MatchParentBottomSheetDialogFragment

/**
 * 搜索页.
 *
 * @author yushaojian
 * @date 2021-05-30 15:30
 */
class SearchDialogFragment : MatchParentBottomSheetDialogFragment() {

    private var binding by autoCleared<SearchFragmentBinding>()
    private val viewModel by viewModels<SearchViewModel> { getViewModelFactory() }
    private val searchResultAdapter = SearchResultAdapter(
        {
            findNavController().viewSite(requireActivity(), it)
            // 埋点
            reportAppClick(PAGE_SEARCH, it.name)
            MarketEventHelper.onFunctionUseComplete()
        },
        { findNavController().navigate(NavGraphDirections.toWebViewFragment("", it)) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SearchFragmentBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            adapter = searchResultAdapter
            viewmodel = viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchET.performClick()
        binding.searchET.addTextChangedListener(
            onTextChanged = { text, _, _, _ ->
                viewModel.search(text.toString())
            }
        )
        viewModel.items.observe(viewLifecycleOwner) {
            searchResultAdapter.submitList(it)
        }
        val layoutManager = binding.resultsRV.layoutManager as GridLayoutManager
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (searchResultAdapter.getItemViewType(position)) {
                    R.layout.item_search_result -> 1
                    else -> layoutManager.spanCount
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        SiteEnterAd.load(requireActivity())

        // 埋点
        reportPageShow(PAGE_SEARCH)
    }
}