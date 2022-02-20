package com.smart.browser.little.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.smart.browser.little.ad.SiteEnterAd
import com.smart.browser.little.data.R
import com.smart.browser.little.databinding.GamesOfCategoryFragmentBinding
import com.smart.browser.little.report.PAGE_GAMES_OF_CATEGORY
import com.smart.browser.little.report.reportGameClick
import com.smart.browser.little.report.reportPageShow
import com.smart.browser.little.ui.getViewModelFactory
import com.smart.browser.little.ui.viewGame
import com.smart.browser.little.util.autoCleared
import com.smart.browser.little.market.MarketEventHelper
import com.art.maker.data.adapter.SitesAdapter
import com.art.maker.data.adapter.SpacerItemDecoration
import com.art.maker.data.model.Site

/**
 * 某一分类下的所有Apps页面.
 *
 * @author yushaojian
 * @date 2021-05-31 09:16
 */
class GamesOfCategoryFragment : Fragment() {

    private var binding by autoCleared<GamesOfCategoryFragmentBinding>()
    private val viewModel by viewModels<GamesOfCategoryViewModel> { getViewModelFactory() }
    private val args by navArgs<GamesOfCategoryFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = GamesOfCategoryFragmentBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            adapter = object : SitesAdapter() {
                override fun onAppClick(site: Site) {
                    findNavController().viewGame(requireActivity(), site)

                    // 埋点
                    reportGameClick(PAGE_GAMES_OF_CATEGORY, site.name)
                    MarketEventHelper.onFunctionUseComplete()
                }
            }
            viewmodel = viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spacing = resources.getDimensionPixelSize(R.dimen.game_item_spacing)
        binding.recyclerView.addItemDecoration(SpacerItemDecoration(spacing))

        viewModel.start(args.key)
    }

    override fun onResume() {
        super.onResume()
        SiteEnterAd.load(requireActivity())

        // 埋点
        reportPageShow(PAGE_GAMES_OF_CATEGORY)
    }
}