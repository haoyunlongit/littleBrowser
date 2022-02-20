package com.smart.browser.little.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.smart.browser.little.databinding.AppsOfCategoryFragmentBinding
import com.smart.browser.little.report.PAGE_APPS_OF_CATEGORY
import com.smart.browser.little.report.reportAppClick
import com.smart.browser.little.report.reportPageShow
import com.smart.browser.little.ui.getViewModelFactory
import com.smart.browser.little.ui.viewSite
import com.smart.browser.little.util.autoCleared
import com.smart.browser.little.market.MarketEventHelper
import com.art.maker.data.adapter.SitesAdapter
import com.art.maker.data.model.Site

/**
 * 某一分类下的所有Apps页面.
 *
 * @author yushaojian
 * @date 2021-05-31 09:16
 */
class AppsOfCategoryFragment : Fragment() {

    private var binding by autoCleared<AppsOfCategoryFragmentBinding>()
    private val viewModel by viewModels<AppsOfCategoryViewModel> { getViewModelFactory() }
    private val args by navArgs<AppsOfCategoryFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = AppsOfCategoryFragmentBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            adapter = object : SitesAdapter() {
                override fun onAppClick(site: Site) {
                    findNavController().viewSite(requireActivity(), site)

                    // 埋点
                    reportAppClick(PAGE_APPS_OF_CATEGORY, site.name)
                    MarketEventHelper.onFunctionUseComplete()
                }
            }
            viewmodel = viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.start(args.key)
    }

    override fun onResume() {
        super.onResume()

        // 埋点
        reportPageShow(PAGE_APPS_OF_CATEGORY)
    }

}