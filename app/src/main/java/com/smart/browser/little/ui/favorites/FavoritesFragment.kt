package com.smart.browser.little.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.smart.browser.little.ad.FavoriteNativeAd
import com.smart.browser.little.ad.SiteEnterAd
import com.smart.browser.little.databinding.FragmentFavoritesBinding
import com.smart.browser.little.report.PAGE_FAVORITE
import com.smart.browser.little.report.reportAppClick
import com.smart.browser.little.report.reportPageShow
import com.smart.browser.little.ui.getViewModelFactory
import com.smart.browser.little.ui.viewSite
import com.smart.browser.little.util.autoCleared
import com.smart.browser.little.market.MarketEventHelper
import com.art.maker.ad.AdListener
import com.art.maker.data.adapter.SitesAdapter
import com.art.maker.data.model.Site

class FavoritesFragment : Fragment() {

    private val viewModel by viewModels<FavoritesViewModel> { getViewModelFactory() }
    private var binding by autoCleared<FragmentFavoritesBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            adapter = object : SitesAdapter() {
                override fun onAppClick(site: Site) {
                    findNavController().viewSite(requireActivity(), site)

                    // 埋点
                    reportAppClick(PAGE_FAVORITE, site.name)
                    MarketEventHelper.onFunctionUseComplete()
                }
            }
            viewmodel = viewModel
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        SiteEnterAd.load(requireActivity())
        addNativeAd()

        // 埋点
        reportPageShow(PAGE_FAVORITE)
    }

    private fun addNativeAd() {
        val viewGroup = binding.adLayout
        if (viewGroup.childCount > 0) {
            return // 已经添加了广告，不刷新
        }

        if (FavoriteNativeAd.hasCache()) {
            binding.divider.visibility = View.VISIBLE
            FavoriteNativeAd.show(viewLifecycleOwner.lifecycle, viewGroup)
        } else {
            FavoriteNativeAd.addAdListener(object : AdListener() {
                override fun onAdLoaded(oid: String) {
                    if (!isAdded) return
                    binding.divider.visibility = View.VISIBLE
                    FavoriteNativeAd.show(viewLifecycleOwner.lifecycle, viewGroup)
                }
            })
            FavoriteNativeAd.load(requireActivity())
        }
    }

    override fun onDestroyView() {
        FavoriteNativeAd.removeAdListeners()
        super.onDestroyView()
    }

}