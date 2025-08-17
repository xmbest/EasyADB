package com.xmbest.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xmbest.Config
import com.xmbest.component.ButtonItem
import com.xmbest.component.SettingsItem
import com.xmbest.locale.rememberPropertiesLocalization
import com.xmbest.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen() {
    val viewModel = viewModel { SettingsViewModel() }
    val theme = viewModel.theme.collectAsState()
    val adb = viewModel.adbPath.collectAsState().value

    val strings = rememberPropertiesLocalization(locale = Config.locale.value)

    SettingsItem(
        title = strings.get("theme.setting"),
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp, start = 12.dp)
    ) {
        Row(
            modifier = Modifier.clip(RoundedCornerShape(8.dp)).height(44.dp)
        ) {
            viewModel.themeList.forEach { item ->
                ButtonItem(item.first, item.second == theme.value) {
                    viewModel.changeTheme(item.second)
                }
            }
        }
    }

    SettingsItem(
        title = strings.get("adb.config"),
        modifier = Modifier.fillMaxWidth().padding(start = 12.dp, top = 6.dp)
    ) {
        Row(
            modifier = Modifier.clip(RoundedCornerShape(8.dp)).height(44.dp)
        ) {
            viewModel.envList.forEach { item ->
                ButtonItem(item.first, item.second.path == adb) {
                    viewModel.changeAdbEnv(item.second)
                }
            }
        }
    }
}

