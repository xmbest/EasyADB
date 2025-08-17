package com.xmbest.screen.router

import com.android.ddmlib.IDevice

data class RouterUiState(
    /**
     * 当前选择item
     */
    val index: Int = 0,
    /**
     * 是否展示设备列表
     */
    val devicesListShow: Boolean = false,
    val device: IDevice? = null,
    val devices: List<IDevice> = emptyList()
)
