package com.smart.browser.little.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

/**
 * ViewBinding Activity基类.
 */
abstract class BindingActivity<Binding : ViewBinding> : AppCompatActivity() {

    private var _binding: Binding? = null
    protected val binding get() = _binding!! // 切记，仅在onCreateView和onDestroyView之间可用

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = createBinding(layoutInflater, null)
        setContentView(binding.root)

        initViews()
        initObservers()
    }

    protected abstract fun createBinding(inflater: LayoutInflater, container: ViewGroup?): Binding

    protected open fun initViews() {}

    protected open fun initObservers() {}

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}