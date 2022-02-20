package com.smart.browser.little.ui.download.manager

import androidx.lifecycle.*
import com.smart.browser.little.Event
import com.art.vd.model.Video
import kotlinx.coroutines.launch

/**
 * 下载管理页ViewModel.
 *
 * @author yushaojian
 * @date 2021-12-19 17:31
 */
class DownloadsViewModel : ViewModel() {

    private val _items = MutableLiveData<List<Video>>()
    val items: LiveData<List<Video>> = _items

    val empty = _items.map { it.isEmpty() }

    private val _watchVideoEvent = MutableLiveData<Event<Video>>()
    val watchVideoEvent: LiveData<Event<Video>> = _watchVideoEvent

    init {
        viewModelScope.launch {
            _items.value = DownloadsManager.read()
        }
    }

    fun watch(video: Video) {
        _watchVideoEvent.value = Event(video)
    }

}