package com.xmbest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xmbest.Config
import com.xmbest.ddmlib.DeviceManager
import com.xmbest.locale.PropertiesLocalization
import com.xmbest.model.Environment
import com.xmbest.model.Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    val strings = PropertiesLocalization.create(Config.STRINGS_NAME, Config.locale.value)

    val themeList = listOf(
        Pair(strings.get("theme.light"), Theme.Light),
        Pair(strings.get("theme.night"), Theme.Dark),
        Pair(strings.get("theme.system"), Theme.System)
    )

    val envList = listOf(
        Pair(strings.get("env.system"), Environment.Program),
        Pair(strings.get("env.program"), Environment.System),
        Pair(strings.get("env.custom"), Environment.Custom(""))
    )

    val theme = Config.theme

    val adbPath = DeviceManager.adbPath

    fun changeAdbEnv(environment: Environment) {
        viewModelScope.launch(Dispatchers.IO) {
            DeviceManager.initialize(environment.path)
        }
    }

    fun changeTheme(newTheme: Theme) {
        viewModelScope.launch(Dispatchers.IO) {
            Config.changeTheme(newTheme)
        }
    }

}