package com.xmbest.platform.impl

import com.xmbest.GlobalManager
import com.xmbest.model.Device
import com.xmbest.platform.IADBPlatform

class MacIADBPlatform(override var device: Device? = null) : IADBPlatform {
    override val adb: String
        get() = GlobalManager.getAdbAbsolutePath()

    override fun shell(cmd: String) = adb("shell $cmd")
}