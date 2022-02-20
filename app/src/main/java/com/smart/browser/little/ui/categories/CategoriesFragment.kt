package com.smart.browser.little.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.smart.browser.little.EventObserver
import com.smart.browser.little.NavGraphDirections
import com.smart.browser.little.R
import com.smart.browser.little.ad.SeeAllRewardAd
import com.smart.browser.little.databinding.CategoriesFragmentBinding
import com.smart.browser.little.manager.unlockingCategoryKey
import com.smart.browser.little.report.PAGE_CATEGORY
import com.smart.browser.little.report.reportCategoryClick
import com.smart.browser.little.report.reportPageShow
import com.smart.browser.little.ui.dialog.AlertDialogFragment
import com.smart.browser.little.ui.getViewModelFactory
import com.smart.browser.little.util.autoCleared
import com.art.maker.util.isNetworkAvailable

/**
 * 分类页.
 *
 * @author yushaojian
 * @date 2021-05-31 06:39
 */
class CategoriesFragment : Fragment() {

    private var binding by autoCleared<CategoriesFragmentBinding>()
    private val viewModel by viewModels<CategoriesViewModel> { getViewModelFactory() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = CategoriesFragmentBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            adapter = object : AppCategoriesAdapter() {
                override fun onCategoryClick(statefulCategory: StatefulCategory) {
                    val category = statefulCategory.category
                    if (statefulCategory.unlocked) {
                        findNavController().navigate(CategoriesFragmentDirections.toAppsOfCategory(category.name, category.key))

                        // 埋点
                        reportCategoryClick(PAGE_CATEGORY, category.name)
                    } else {
                        unlockingCategoryKey = category.key
                        val title = getString(R.string.alert)
                        val message = getString(R.string.watch_video_to_unlock)
                        val positive = getString(R.string.confirm)
                        val negative = getString(R.string.cancel)
                        findNavController().navigate(NavGraphDirections.toAlertDialog(title, message, positive, negative))
                    }
                }
            }
            viewmodel = viewModel
            recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadSeeAllRewardAdEvent.observe(viewLifecycleOwner, EventObserver {
            SeeAllRewardAd.load(requireActivity())
        })
        setFragmentResultListener(AlertDialogFragment.ALERT_DIALOG_REQUEST) { _, bundle ->
            val accepted = bundle.getBoolean(AlertDialogFragment.ALERT_DIALOG_RESULT, false)
            if (accepted) {
                unlock()
            } else {
                unlockingCategoryKey = null
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // 埋点
        reportPageShow(PAGE_CATEGORY)
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
}