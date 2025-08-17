package com.xmbest.screen.router

import com.android.ddmlib.IDevice

sealed class RouterUiEvent {
    class SelectLeftItem(val index: Int) : RouterUiEvent()
    class SelectDevice(val device: IDevice) : RouterUiEvent()
    class ShowDeviceList(val show: Boolean) : RouterUiEvent()
    object RefreshDevice : RouterUiEvent()
}