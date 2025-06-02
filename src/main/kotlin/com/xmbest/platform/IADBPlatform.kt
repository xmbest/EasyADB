package com.xmbest.platform

import com.xmbest.model.Device
import com.xmbest.utils.ProcessExecutor

interface IADBPlatform {

    companion object {
        fun killServer() = ProcessExecutor.executeAndReadOutput("adb kill-server")
        fun devices(): List<String> {
            val list = mutableListOf<String>()
            val devices: String = ProcessExecutor.executeAndReadOutput("adb devices")
            val splitList = devices.trim().split("\n")
            //只列出活跃(device)的设备
            for (i in 1 until splitList.size) {
                val element = splitList[i]
                if (element.contains("device")) {
                    val device = element.replace("device", "").trim()
                    list.add(device)
                }
            }
            return list
        }
    }

    /**
     * 执行adb命令的设备
     */
    var device: Device?
    fun adb(cmd: String) =
        ProcessExecutor.executeAndReadOutput("adb ${if (device == null) "" else "-s ${device!!.name}"} $cmd").trim()

    fun getAdbExecutableName(): String = "adb"
    fun shell(cmd: String): String
    fun reboot() = adb("reboot")
    fun root() = adb("root")
    fun tcpip(port: Int = 5555) = adb("tcpip $port")
    fun inputEvent(key: Int) = shell("input event $key")
    fun forceStop(packageName: String) = shell("am force-stop $packageName")
    fun kill(packageName: String) = shell("killall $packageName")
    fun clear(packageName: String) = shell("pm clear $packageName")
    fun install(path: String) = adb("install $path")
    fun uninstall(packageName: String) = adb("uninstall $packageName")
    fun touch(fileName: String) = shell("touch $fileName")
    fun mkdir(dir: String) = shell("mkdir -p $dir")
    fun cp(start: String, end: String) = shell("cp $start,$end")
    fun mv(start: String, end: String) = shell("mv $start,$end")
    fun rm(fileName: String) = shell("rm $fileName")
    fun getProp(key: String): String = shell("getprop $key")
}