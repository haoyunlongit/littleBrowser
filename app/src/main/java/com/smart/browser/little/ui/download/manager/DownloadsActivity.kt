package com.smart.browser.little.ui.download.manager

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import com.smart.browser.little.EventObserver
import com.smart.browser.little.R
import com.smart.browser.little.databinding.DownloadsActivityBinding
import com.smart.browser.little.ui.base.BindingActivity
import com.art.player.PlayerActivity

/**
 * 下载管理页.
 */
class DownloadsActivity : BindingActivity<DownloadsActivityBinding>() {

    private val viewModel by viewModels<DownloadsViewModel>()
    private lateinit var adapter: DownloadsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?) =
        DownloadsActivityBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@DownloadsActivity
            viewmodel = viewModel
        }

    override fun initViews() {
        adapter = DownloadsAdapter(this@DownloadsActivity, viewModel)
        binding.downloadsRV.adapter = adapter
    }

    override fun initObservers() {
        viewModel.items.observe(this@DownloadsActivity) {
            adapter.setItems(it)
        }

        viewModel.watchVideoEvent.observe(this@DownloadsActivity, EventObserver {
            try {
                val intent = Intent(this, PlayerActivity::class.java)
                intent.setDataAndType(Uri.parse(it.downloadUrl), "video/mp4")
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(applicationContext, R.string.something_error_retry, Toast.LENGTH_SHORT).show()
            }
        })
    }

}