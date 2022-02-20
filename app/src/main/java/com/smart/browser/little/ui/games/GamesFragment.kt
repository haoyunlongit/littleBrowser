package com.smart.browser.little.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smart.browser.little.NavGraphDirections
import com.smart.browser.little.ad.HomeGamesAd
import com.smart.browser.little.ad.RecycleAdvertHelper
import com.smart.browser.little.ad.SiteEnterAd
import com.smart.browser.little.databinding.FragmentGamesBinding
import com.smart.browser.little.market.MarketEventHelper
import com.smart.browser.little.report.PAGE_GAME
import com.smart.browser.little.report.reportGameClick
import com.smart.browser.little.report.reportPageShow
import com.smart.browser.little.ui.getViewModelFactory
import com.smart.browser.little.ui.viewGame
import com.smart.browser.little.util.autoCleared
import com.art.maker.data.model.Site
import com.art.maker.data.model.SiteSection
import com.art.maker.util.Log

class GamesFragment : Fragment() {

    private val viewModel by viewModels<GamesViewModel> { getViewModelFactory() }
    private var binding by autoCleared<FragmentGamesBinding>()
    private var sectionAdapter by autoCleared<GameSectionAdapter>()
    private val advertHelper = RecycleAdvertHelper()
    private var layoutManager: LinearLayoutManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGamesBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager
        sectionAdapter = object : GameSectionAdapter() {

            override fun onCategoryClick(name: String, key: String) {
                findNavController().navigate(NavGraphDirections.toGamesOfCategory(name, key))
            }

            override fun onAppClick(section: SiteSection, site: Site) {
                findNavController().viewGame(requireActivity(), site)

                // 埋点
                reportGameClick(PAGE_GAME, site.name)
                MarketEventHelper.onFunctionUseComplete()
            }
        }
        binding.recyclerView.adapter = sectionAdapter

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!isAdded || !isResumed) return
                updateAdByScroll(recyclerView, newState)
            }
        })
        viewModel.dataLoading.observe(viewLifecycleOwner){
            Log.d("reloadAdIfNull", "GamesFragment dataLoading it=$it isResumed=${isResumed}")
            if(!it && isResumed){
                Log.d("reloadAdIfNull", "GamesFragment reloadAdIfNull dataloading false start")
                reloadAdIfNull()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        SiteEnterAd.load(requireActivity())
        HomeGamesAd.load(requireActivity())

        // 埋点
        reportPageShow(PAGE_GAME)
        if(viewModel.dataLoading.value == false){
            Log.d("reloadAdIfNull", "GamesFragment reloadAdIfNull onResume start")
            reloadAdIfNull()
        }

    }

    fun reloadAdIfNull(){
        advertHelper.reloadAdIfNull<PlaceholderItem, AdItem>(
            requireActivity(),
            this,
            viewModel.mainScope,
            layoutManager!!,
            { viewModel.items.value },
            { HomeGamesAd },
            { HomeGamesAd.getAd() },
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
            { HomeGamesAd.getAd() },
            { HomeGamesAd.load(it) },
            { AdItem(it) },
            { pos, ad -> sectionAdapter.replace(pos, ad) }
        )
    }
}