package com.xmbest.model

import com.xmbest.platform.impl.MacIADBPlatform
import com.xmbest.platform.impl.WindowsIADBPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.skiko.hostOs

data class Device(
    val name: String,
    val serialNo: MutableStateFlow<String> = MutableStateFlow(""),
    val ip: MutableStateFlow<String> = MutableStateFlow(""),
    val memory: MutableStateFlow<String> = MutableStateFlow(""),
    val density: MutableStateFlow<String> = MutableStateFlow(""),
    val cpu: MutableStateFlow<String> = MutableStateFlow(""),
    val model: MutableStateFlow<String> = MutableStateFlow(""),
    val brand: MutableStateFlow<String> = MutableStateFlow(""),
    val androidVersion: MutableStateFlow<String> = MutableStateFlow(""),
    val isRoot: MutableStateFlow<Boolean> = MutableStateFlow(false),
    val isRemount: MutableStateFlow<Boolean> = MutableStateFlow(false)
) {
    val adb = if (hostOs.isMacOS) MacIADBPlatform(this) else WindowsIADBPlatform(this)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            serialNo.update { adb.getProp("ro.serialno") }
            ip.update { adb.shell("ifconfig wlan0 |  grep addr:1 |  awk  '{print $2}'") }
            density.update {
                adb.shell("wm size | awk '{print \$NF}'") + "(" + "dpi = " +
                        adb.shell("wm density")
                            .replace("Physical density: ", "") + ")"
            }
            model.update { adb.getProp("ro.product.model") }
            brand.update { adb.getProp("ro.product.brand") }
            memory.update {
                adb.shell("cat /proc/meminfo | grep MemTotal | awk '{print $2/1024}'") + "MB"
            }
            androidVersion.update {
                adb.getProp("ro.vendor.build.version.release")
                    .ifBlank { adb.getProp("ro.build.version.release") }
            }
            cpu.update {
                adb.getProp("ro.soc.model") + "(" +
                        adb.getProp("ro.product.cpu.abi") + ",core size = " +
                        adb.shell("cat /proc/cpuinfo | grep processor | wc -l") + ")"
            }
            isRoot.update { adb.shell("id").startsWith("uid=0") }

            isRemount.update { adb.adb("remount").contains("success") }.toString()

            print(this@Device.isRoot.value)
        }

    }
}