package com.smart.browser.little.ui.exitapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.smart.browser.little.Event

/**
 * 退出app确认弹窗ViewModel.
 *
 * @author yushaojian
 * @date 2021-06-16 21:21
 */
class ExitAppViewModel : ViewModel() {

    private val _exitAppEvent = MutableLiveData<Event<Unit>>()
    val exitAppEvent: LiveData<Event<Unit>> = _exitAppEvent

    fun exitApp() {
        _exitAppEvent.value = Event(Unit)
    }
}