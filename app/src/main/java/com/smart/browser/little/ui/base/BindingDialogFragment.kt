package com.smart.browser.little.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.viewbinding.ViewBinding

/**
 * ViewBinding Fragment基类.
 *
 * @author yushaojian
 * @date 2021-12-19 12:15
 */
abstract class BindingDialogFragment<Binding : ViewBinding> : AppCompatDialogFragment() {

    private var _binding: Binding? = null
    protected val binding get() = _binding!! // 切记，仅在onCreateView和onDestroyView之间可用

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = createBinding(inflater, container)
        return binding.root
    }

    protected abstract fun createBinding(inflater: LayoutInflater, container: ViewGroup?): Binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initObservers()
    }

    protected open fun initViews() {}

    protected open fun initObservers() {}

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}