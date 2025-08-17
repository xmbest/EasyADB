package com.xmbest.screen.router

import androidx.lifecycle.viewModelScope
import com.android.ddmlib.IDevice
import com.xmbest.base.BaseViewModel
import com.xmbest.ddmlib.DeviceManager
import com.xmbest.model.Page
import com.xmbest.screen.settings.SettingsScreen
import com.xmbest.utils.ResourceUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RouterViewModule() : BaseViewModel<RouterUiState>() {
    val pageList = listOf(
        Page(
            getString("router.item.settings"),
            ResourceUtil.iconPath("settings")
        ) { SettingsScreen() },
        Page(
            getString("router.item.settings") + "1",
            ResourceUtil.iconPath("settings")
        ) { SettingsScreen() },
    )

    override val _uiState = MutableStateFlow(RouterUiState())

    init {
        viewModelScope.launch(Dispatchers.Default) {
            DeviceManager.device.collectLatest {
                _uiState.value = _uiState.value.copy(device = it)
            }
        }

        viewModelScope.launch(Dispatchers.Default) {
            DeviceManager.devices.collectLatest {
                _uiState.value = _uiState.value.copy(devices = it)
            }
        }
    }

    fun onEvent(event: RouterUiEvent) {
        when (event) {
            is RouterUiEvent.SelectLeftItem -> selectLeftItem(event.index)
            is RouterUiEvent.SelectDevice -> selectDevice(event.device)
            is RouterUiEvent.ShowDeviceList -> showDeviceList(event.show)
            is RouterUiEvent.RefreshDevice -> refreshDevice()
        }
    }

    private fun selectLeftItem(pageIndex: Int) {
        _uiState.value = _uiState.value.copy(index = pageIndex)
    }

    private fun showDeviceList(show: Boolean) {
        _uiState.value = _uiState.value.copy(devicesListShow = show)
    }

    private fun selectDevice(iDevice: IDevice) {
        DeviceManager.changeDevice(iDevice)
        showDeviceList(false)
    }

    private fun refreshDevice() {
        DeviceManager.refreshDevices()
    }

}