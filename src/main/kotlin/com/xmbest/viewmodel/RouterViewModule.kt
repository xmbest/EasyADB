package com.xmbest.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.xmbest.screen.SettingsScreen
import com.xmbest.utils.ResourceUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class Page(val name: String, val icon: String, val comp: @Composable () -> Unit)

class RouterViewModule(): ViewModel() {
    val pageList = listOf(
        Page("设置", ResourceUtil.iconPath("settings")) { SettingsScreen() },
        Page("测试", ResourceUtil.iconPath("android")) { SettingsScreen() },
        Page("测试", ResourceUtil.iconPath("android")) { SettingsScreen() },
        Page("测试", ResourceUtil.iconPath("android")) { SettingsScreen() },
        Page("测试", ResourceUtil.iconPath("android")) { SettingsScreen() },
        Page("测试", ResourceUtil.iconPath("android")) { SettingsScreen() },
    )
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