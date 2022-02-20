package com.smart.browser.little.ui.download.choice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.smart.browser.little.Event
import com.smart.browser.little.report.reportEvent
import com.art.vd.model.Video

/**
 * 下载选项ViewModel.
 *
 * @author yushaojian
 * @date 2021-12-19 16:31
 */
class DownloadChoiceViewModel : ViewModel() {

    private val _checkedItems = MutableLiveData<List<Video>>()
    val hasCheckedItems = _checkedItems.map { it.isNotEmpty() }

    private val _toDownloadsEvent = MutableLiveData<Event<List<Video>>>()
    val toDownloadsEvent: LiveData<Event<List<Video>>> = _toDownloadsEvent

    fun checked(video: Video) = _checkedItems.map { it.contains(video) }

    fun check(video: Video) {
        val values = _checkedItems.value?.toMutableList() ?: ArrayList()
        if (!values.contains(video)) {
            values.add(video)
            _checkedItems.value = values
        }
    }

    fun uncheck(video: Video) {
        val values = _checkedItems.value?.toMutableList() ?: return
        if (values.contains(video)) {
            values.remove(video)
        }
        _checkedItems.value = values
    }

    fun download() {
        val values = _checkedItems.value ?: return
        _toDownloadsEvent.value = Event(values)
        reportEvent("video_download_dialog_btn_click", type = values.size.toString())
    }
}