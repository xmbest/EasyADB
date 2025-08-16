package com.xmbest.viewmodel

import com.xmbest.GlobalManager
import com.xmbest.model.Environment
import com.xmbest.model.Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    val theme = listOf(
        Pair("浅色", Theme.Light),
        Pair("深色", Theme.Dark),
        Pair("跟随系统", Theme.System)
    )

    val environment = listOf(
        Pair("程序携带", Environment.APP),
        Pair("环境变量", Environment.SYSTEM),
        Pair("自定义", Environment.CUSTOMER)
    )

    val currentTheme = MutableStateFlow(Theme.System)
    val currentEnvironment = MutableStateFlow(Environment.APP)

    init {
        coroutineScope.launch {
            GlobalManager.theme.collect(currentTheme)
        }

        coroutineScope.launch {
            GlobalManager.adbEnvironment.collect(currentEnvironment)
        }

    }

}