package com.xmbest.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RouterViewModule() {
    private val _index = MutableStateFlow(0)
    val index = _index.asStateFlow()

    private val _deviceListShow = MutableStateFlow(false)
    val deviceListShow = _deviceListShow.asStateFlow()

    fun updateIndex(pageIndex: Int) {
        _index.update { pageIndex }
    }

    fun updateDeviceListShow(show: Boolean) {
        _deviceListShow.update { show }
    }

}