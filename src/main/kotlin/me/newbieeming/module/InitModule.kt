package me.newbieeming.module

import io.github.vinceglb.filekit.FileKit
import me.newbieeming.adb
import me.newbieeming.appStorageAbsolutePath
import me.newbieeming.cfg
import me.newbieeming.ddmlib.AdbPathResolver
import me.newbieeming.ddmlib.DeviceManager
import me.newbieeming.exec
import me.newbieeming.model.Environment
import me.newbieeming.util.PreferencesUtil
import me.newbieeming.util.PreferencesUtil.PREFERENCES_ADB_PATH
import me.newbieeming.util.PreferencesUtil.PREFERENCES_CUSTOMER_ADB_PATH
import java.io.File

object InitModule {
    private val fileList = buildList {
        addAll(listOf(adb, cfg, exec))
    }
    private val path = appStorageAbsolutePath

    fun init() {
        writeFile()
        initAdb()
        FileKit.init("EasyADB")
    }

    private fun writeFile() {
        val parentFile = File(path)
        if (!parentFile.exists()) {
            parentFile.mkdirs()
        }
        // 复制所需文件
        fileList.forEach {
            val fileName = it.second
            val file = File(parentFile, fileName)
            if (!file.exists()) {
                file.createNewFile()
                file.setExecutable(true)
                this::class.java.classLoader.getResourceAsStream("${it.first}/$fileName")?.use { input ->
                    input.copyTo(file.outputStream())
                }
            }
        }
    }

    private fun initAdb() {
        val savedAdbPath = PreferencesUtil.get(PREFERENCES_ADB_PATH, "")

        val adbPath = when {
            savedAdbPath.isNotEmpty() -> savedAdbPath
            else -> resolveAndSaveAdbPath()
        }

        DeviceManager.initialize(adbPath)
    }

    private fun resolveAndSaveAdbPath(): String {
        val resolveResult = AdbPathResolver.resolveAdbPath("adb")

        return when (resolveResult.source) {
            AdbPathResolver.AdbSource.PATH_ENV -> {
                PreferencesUtil.set(PREFERENCES_ADB_PATH, Environment.System.path)
                Environment.System.path  // 返回 "adb"，而非 resolveResult.path
            }
            // 从 SDK 默认路径找到：同时写 CUSTOMER_ADB_PATH，保证重启后 TextField 有内容
            AdbPathResolver.AdbSource.SDK_DEFAULT -> {
                Environment.Custom.path = resolveResult.path
                PreferencesUtil.set(PREFERENCES_CUSTOMER_ADB_PATH, resolveResult.path)
                PreferencesUtil.set(PREFERENCES_ADB_PATH, resolveResult.path)
                resolveResult.path
            }
            // 未找到，使用程序自带的 ADB
            AdbPathResolver.AdbSource.FALLBACK -> {
                PreferencesUtil.set(PREFERENCES_ADB_PATH, Environment.Program.path)
                Environment.Program.path
            }
        }
    }
}