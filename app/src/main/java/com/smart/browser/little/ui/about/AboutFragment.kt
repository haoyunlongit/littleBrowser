package com.smart.browser.little.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.smart.browser.little.BuildConfig
import com.smart.browser.little.databinding.FragmentAboutBinding
import com.smart.browser.little.util.autoCleared

class AboutFragment: Fragment() {

    private var binding by autoCleared<FragmentAboutBinding>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvVersion.text = "Version: ${BuildConfig.VERSION_NAME}"
    }
}