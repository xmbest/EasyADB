package com.xmbest.platform.impl

import com.xmbest.model.Device
import com.xmbest.platform.IADBPlatform

class MacIADBPlatform(override var device: Device? = null) : IADBPlatform {
    override fun getAdbExecutableName() = "adb.exe"
    override fun shell(cmd: String) = adb("shell $cmd")
}