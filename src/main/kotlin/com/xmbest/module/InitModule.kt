package com.xmbest.module

import androidx.compose.ui.res.useResource
import com.alibaba.fastjson2.JSONObject
import com.xmbest.adb
import com.xmbest.appStorageAbsolutePath
import com.xmbest.cfg
import com.xmbest.ddmlib.DeviceManager
import com.xmbest.model.Environment
import com.xmbest.model.Config
import java.io.File

object InitModule {
    private val fileList = listOf(adb, cfg)
    private val path = appStorageAbsolutePath

    fun init() {
        writeFile()
        loadConfig()
        DeviceManager.initialize(Environment.System.path)
    }

    fun writeFile() {
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
                useResource(it.first + "/" + fileName) {
                    it.copyTo(file.outputStream())
                }
            }
        }
    }


    fun loadConfig() {
        val fileName = cfg.second
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