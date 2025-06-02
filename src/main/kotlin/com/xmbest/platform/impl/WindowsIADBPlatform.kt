package com.xmbest.platform.impl

import com.xmbest.model.Device
import com.xmbest.platform.IADBPlatform

class WindowsIADBPlatform(override var device: Device? = null) : IADBPlatform {
    override fun shell(cmd: String) = adb("shell \"$cmd\"")
}