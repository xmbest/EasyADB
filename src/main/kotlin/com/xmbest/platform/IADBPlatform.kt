package com.xmbest.platform

import com.xmbest.model.Device
import com.xmbest.utils.CmdUtil

interface IADBPlatform {

    val adb: String


    /**
     * 执行adb命令的设备
     */
    var device: Device?
    fun adb(cmd: String) =
        CmdUtil.runAndGetResult("$adb ${if (device == null) "" else "-s ${device!!.name}"} $cmd")
            .trim()

    fun shell(cmd: String): String
    suspend fun reboot() = adb("reboot")
    suspend fun root() = adb("root")
    suspend fun tcpip(port: Int = 5555) = adb("tcpip $port")
    suspend fun inputEvent(key: Int) = shell("input event $key")
    suspend fun forceStop(packageName: String) = shell("am force-stop $packageName")
    suspend fun kill(packageName: String) = shell("killall $packageName")
    suspend fun clear(packageName: String) = shell("pm clear $packageName")
    suspend fun install(path: String) = adb("install $path")
    suspend fun uninstall(packageName: String) = adb("uninstall $packageName")
    suspend fun touch(fileName: String) = shell("touch $fileName")
    suspend fun mkdir(dir: String) = shell("mkdir -p $dir")
    suspend fun cp(start: String, end: String) = shell("cp $start,$end")
    suspend fun mv(start: String, end: String) = shell("mv $start,$end")
    suspend fun rm(fileName: String) = shell("rm $fileName")
    suspend fun getProp(key: String): String = shell("getprop $key")
}