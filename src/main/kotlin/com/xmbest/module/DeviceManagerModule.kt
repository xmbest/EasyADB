package com.xmbest.module

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import com.xmbest.model.Device
import com.xmbest.platform.IADBPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

object DeviceManagerModule {
    private val _device = MutableStateFlow<Device?>(null)
    val device = _device.asStateFlow()
    private val _devices = MutableStateFlow<Set<Device>>(emptySet())
    val devices = _devices.asStateFlow()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null

    init {
        initAdbListener()
    }

    private fun initAdbListener() {
        AndroidDebugBridge.addDeviceChangeListener(object : AndroidDebugBridge.IDeviceChangeListener {
            override fun deviceConnected(device: IDevice?) {
                println("deviceConnected device $device")
                refreshDevices()
            }

            override fun deviceDisconnected(device: IDevice?) {
                println("deviceDisconnected device $device")
                refreshDevices()
            }

            override fun deviceChanged(device: IDevice?, changeMask: Int) {
                println("changed device $device, changeMask $changeMask")
                if (IDevice.CHANGE_CLIENT_LIST == changeMask) {
                    refreshDevices()
                }
            }
        })
        AndroidDebugBridge.init(false)
        AndroidDebugBridge.createBridge(5000, TimeUnit.SECONDS)
    }

    /**
     * 刷新设备
     */
    fun refreshDevices() {
        job?.cancel()
        job = coroutineScope.launch {
            val set = mutableSetOf<Device>()
            IADBPlatform.Companion.devices().forEach { deviceName ->
                set.add(devices.value.firstOrNull { it.name == deviceName } ?: Device(deviceName))
            }
            _devices.update { set }
            if (devices.value.isEmpty()) {
                _device.update { null }
            } else if (device.value == null || !set.contains(device.value)) {
                _device.update { devices.value.first() }
            }
        }
    }

    fun changeDevice(deviceName: String) {
        println("changeDevice $deviceName")
        if (deviceName == device.value?.name) return
        devices.value.firstOrNull { it.name == deviceName }?.let { value ->
            _device.update { value }
        }
    }
}