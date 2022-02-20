package com.smart.browser.little.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smart.browser.little.EventObserver
import com.smart.browser.little.NavGraphDirections
import com.smart.browser.little.R
import com.smart.browser.little.ad.*
import com.smart.browser.little.databinding.FragmentHomeBinding
import com.smart.browser.little.manager.unlockingCategoryKey
import com.smart.browser.little.report.PAGE_HOME
import com.smart.browser.little.report.reportAppClick
import com.smart.browser.little.report.reportCategoryClick
import com.smart.browser.little.report.reportPageShow
import com.smart.browser.little.ui.dialog.AlertDialogFragment
import com.smart.browser.little.ui.getViewModelFactory
import com.smart.browser.little.ui.viewSite
import com.smart.browser.little.util.autoCleared
import com.smart.browser.little.market.MarketEventHelper
import com.smart.browser.little.ui.download.manager.DownloadsActivity
import com.smart.browser.little.ui.download.manager.isVideoDownloadABEnabled
import com.art.maker.data.model.Site
import com.art.maker.util.Log
import com.art.maker.util.isNetworkAvailable
import com.github.shadowsocks.bg.BaseService
import com.sea.proxy.ProxyInit
import com.sea.proxy.activity.ProxyMainActivity

class HomeFragment : Fragment() {

    private val viewModel by viewModels<HomeViewModel> { getViewModelFactory() } // app不退出，数据不销毁
    private var binding by autoCleared<FragmentHomeBinding>()
    private val mSSViewModel by lazy { ProxyInit.ssViewModelInstance }
    private val advertHelper = RecycleAdvertHelper()
    private val unlock = { keyOfCategory: String ->
        unlockingCategoryKey = keyOfCategory
        val title = getString(R.string.alert)
        val message = getString(R.string.watch_video_to_unlock)
        val positive = getString(R.string.confirm)
        val negative = getString(R.string.cancel)
        findNavController().navigate(
            NavGraphDirections.toAlertDialog(
                title,
                message,
                positive,
                negative
            )
        )
    }

    private var layoutManager: LinearLayoutManager? = null

    private val openCategory = { name: String, key: String ->
        findNavController().navigate(HomeFragmentDirections.toAppsOfCategory(name, key))

        // 埋点
        reportCategoryClick(PAGE_HOME, name)
    }
    private val openApp = { site: Site ->
        findNavController().viewSite(requireActivity(), site)

        // 埋点
        reportAppClick(PAGE_HOME, site.name)
        MarketEventHelper.onFunctionUseComplete()
    }
    private var sectionAdapter by autoCleared<HomeAdapter>()

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            findNavController().navigate(
                NavGraphDirections.toExitAppDialog()
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProxyInit.INSTANCE.dealProxyThingsWhenOpen(requireActivity(), this, "home")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = viewModel
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager
        sectionAdapter = HomeAdapter(
            unlock,
            openCategory,
            openApp
        )
        binding.recyclerView.adapter = sectionAdapter
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!isAdded || !isResumed) return
                updateAdByScroll(recyclerView, newState)
            }
        })
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backPressedCallback
        )
        mSSViewModel.mProxyState.observe(viewLifecycleOwner) {
            requireActivity().invalidateOptionsMenu()
        }
        mSSViewModel.mProxySwitch.observe(viewLifecycleOwner) {
            requireActivity().invalidateOptionsMenu()
        }
        viewModel.loadSeeAllRewardAdEvent.observe(viewLifecycleOwner, EventObserver {
            SeeAllRewardAd.load(requireActivity())
        })


        viewModel.dataLoading.observe(viewLifecycleOwner){
            Log.d("reloadAdIfNull", "HomeFragment dataLoading it=$it isResumed=${isResumed}")
            if(!it && isResumed){
                Log.d("reloadAdIfNull", "HomeFragment reloadAdIfNull dataloading false start")
                reloadAdIfNull()
            }
        }
        setFragmentResultListener(AlertDialogFragment.ALERT_DIALOG_REQUEST) { _, bundle ->
            val accepted = bundle.getBoolean(AlertDialogFragment.ALERT_DIALOG_RESULT, false)
            if (accepted) {
                unlock()
            } else {
                unlockingCategoryKey = null
            }
        }
    }

    private fun unlock() {
        val show = SeeAllRewardAd.show(requireActivity())
        if (!show) {
            val applicationContext = requireContext().applicationContext
            val msgId = if (isNetworkAvailable(applicationContext)) {
                R.string.something_error_retry
            } else {
                R.string.connection_error
            }
            Toast.makeText(applicationContext, msgId, Toast.LENGTH_SHORT).show()
            SeeAllRewardAd.load(requireActivity())
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        val menuItem = menu.findItem(R.id.item_proxy)
        menuItem.isVisible = mSSViewModel.mProxySwitch.value ?: false
        menuItem.setIcon(
            if (mSSViewModel.mProxyState.value == BaseService.State.Connected)
                R.drawable.ic_protect_white
            else
                R.drawable.ic_protect_close
        )

        val vdMenuItem = menu.findItem(R.id.item_downloads)
        vdMenuItem.isVisible = isVideoDownloadABEnabled()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_search -> {
                findNavController().navigate(HomeFragmentDirections.toSearchDialog())
                return true
            }
            R.id.item_proxy -> {
                requireActivity().startActivity(Intent(requireActivity(), ProxyMainActivity::class.java))
                return true
            }
            R.id.item_downloads -> {
                startActivity(Intent(requireActivity(), DownloadsActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity()
        SiteEnterAd.load(activity)
        ExitAppAd.load(activity)
        HomeFeedAd.load(activity)

        // 埋点
        reportPageShow(PAGE_HOME)
        if(viewModel.dataLoading.value == false){
            Log.d("reloadAdIfNull", "HomeFragment reloadAdIfNull onResume start")
            reloadAdIfNull()
        }
    }

    fun reloadAdIfNull(){
        advertHelper.reloadAdIfNull<PlaceholderItem, AdItem>(
            requireActivity(),
            this,
            viewModel.mainScope,
            layoutManager!!,
            { viewModel.homeItems.value },
            { HomeFeedAd },
            { HomeFeedAd.getAd() },
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
            { HomeFeedAd.getAd() },
            { HomeFeedAd.load(it) },
            { AdItem(it) },
            { pos, ad -> sectionAdapter.replace(pos, ad) }
        )
    }
}