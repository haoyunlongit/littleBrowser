package com.smart.browser.little.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.smart.browser.little.databinding.FeedFragmentBinding
import com.smart.browser.little.util.autoCleared
import com.taboola.android.TBLClassicPage
import com.taboola.android.TBLClassicUnit
import com.taboola.android.Taboola
import com.taboola.android.annotations.TBL_PLACEMENT_TYPE.PAGE_BOTTOM
import com.taboola.android.listeners.TBLClassicListener

/**
 * 信息流页.
 *
 * @author yushaojian
 * @date 2021-07-28 07:43
 */
class FeedFragment : Fragment() {

    private var binding by autoCleared<FeedFragmentBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FeedFragmentBinding.inflate(inflater, container, false)
        val classicUnit = getTaboolaUnit()
        binding.root.addView(classicUnit)
        classicUnit.fetchContent()
        return binding.root
    }

    private fun getTaboolaUnit(): TBLClassicUnit {
        val classicPage: TBLClassicPage = Taboola.getClassicPage(TABOOLA_PAGEURL, TABOOLA_PAGETYPE)
        return classicPage.build(context, TABOOLA_PLACEMENTNAME, TABOOLA_MODE, PAGE_BOTTOM, object : TBLClassicListener() {
            override fun onAdReceiveSuccess() {
                binding.loadingPB.isVisible = false
            }
        })
    }

}