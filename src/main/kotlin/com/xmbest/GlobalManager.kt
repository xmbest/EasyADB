package com.xmbest

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import com.xmbest.model.Device
import com.xmbest.utils.CmdUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.skiko.hostOs
import java.io.File
import java.util.concurrent.TimeUnit

object GlobalManager {
    private val appStorageAbsolutePath =
        File(System.getProperty("user.home"), ".easyAdb").absolutePath

    private val _customAdbPath = MutableStateFlow("")
    val customerPath = _customAdbPath.asStateFlow()
    private val _device = MutableStateFlow<Device?>(null)
    val device = _device.asStateFlow()
    private val _devices = MutableStateFlow<Set<Device>>(emptySet())
    val devices = _devices.asStateFlow()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null

    private val _adbEnvironment = MutableStateFlow(AdbEnvironment.SYSTEM)
    val adbEnvironment = _adbEnvironment.asStateFlow()

    init {
        // TODO 查询当前adb环境，主题
        initAdbListener()
    }

    private fun initAdbListener() {
        AndroidDebugBridge.addDeviceChangeListener(object :
            AndroidDebugBridge.IDeviceChangeListener {
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
                if (IDevice.CHANGE_CLIENT_LIST == changeMask || devices.value.isEmpty()) {
                    refreshDevices()
                }
            }
        })
        AndroidDebugBridge.init(false)
        AndroidDebugBridge.createBridge(getAdbAbsolutePath(), true, 5000, TimeUnit.SECONDS)
    }

    /**
     * 刷新设备
     */
    fun refreshDevices() {
        job?.cancel()
        job = coroutineScope.launch {
            val set = mutableSetOf<Device>()
            CmdUtil.devices().forEach { deviceName ->
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

    fun changeAdbEnv(environment: AdbEnvironment) {
        _adbEnvironment.update { environment }
        initAdbListener()
    }

    fun getAdbAbsolutePath(isWindow: Boolean = hostOs.isWindows): String {
        return when (adbEnvironment.value) {
            AdbEnvironment.CUSTOMER -> customerPath.value
            AdbEnvironment.APP -> File(
                appStorageAbsolutePath,
                if (isWindow) "adb.exe" else "adb"
            ).absolutePath

            else -> "adb"
        }
    }
}

/**
 * APP 程序自带
 * SYSTEM 环境变量
 * CUSTOMER 自定义路径
 */
enum class AdbEnvironment {
    APP,
    SYSTEM,
    CUSTOMER;
}