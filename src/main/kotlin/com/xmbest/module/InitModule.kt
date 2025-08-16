package com.xmbest.module

import androidx.compose.ui.res.useResource
import com.alibaba.fastjson2.JSONObject
import com.xmbest.GlobalManager
import com.xmbest.model.Config
import org.jetbrains.skiko.hostOs
import java.io.File

object InitModule {
    /**
     * first 父目录
     * second windows所需文件名称
     * third 其他平台所需文件名称
     */
    private val adb = Triple("adb", "adb.exe", "adb")
    private val cfg = Triple("config", "config.json", "config.json")
    private val fileList = listOf(adb, cfg)
    private val path = GlobalManager.appStorageAbsolutePath

    fun init() {
        writeFile()
        loadConfig()
    }

    fun writeFile() {
        val parentFile = File(path)
        if (!parentFile.exists()) {
            parentFile.mkdirs()
        }
        // 复制所需文件
        fileList.forEach {
            val fileName = if (hostOs.isWindows) it.second else it.third
            val file = File(parentFile, fileName)
            if (!file.exists()) {
                file.createNewFile()
                file.setExecutable(true)
                useResource(it.first + "/" + fileName) {
                    it.copyTo(file.outputStream())
                }
            }
        }
    }


    fun loadConfig() {
        val fileName = if (hostOs.isWindows) cfg.second else cfg.third
        val file = File(path, fileName)
        file.readText().let {
            runCatching {
                val config = JSONObject.parseObject(it, Config::class.java)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }


    fun persistence(config: Config) {
        val configStr = JSONObject.toJSONString(config)
    }
}