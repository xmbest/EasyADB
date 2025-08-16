package com.xmbest.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.xmbest.GlobalManager
import com.xmbest.component.ButtonItem
import com.xmbest.component.SettingsItem
import com.xmbest.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen() {
    val viewModel = remember { SettingsViewModel() }
    val theme = viewModel.currentTheme.collectAsState()
    val environment = viewModel.currentEnvironment.collectAsState()
    SettingsItem(
        title = "程序主题",
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp, start = 12.dp)
    ) {
        Row(
            modifier = Modifier.clip(RoundedCornerShape(8.dp)).height(44.dp)
        ) {
            viewModel.theme.forEach { item ->
                ButtonItem(item.first, item.second == theme.value) {
                    GlobalManager.changeTheme(item.second)
                }
            }
        }
    }

    SettingsItem(
        title = "adb配置",
        modifier = Modifier.fillMaxWidth().padding(start = 12.dp, top = 6.dp)
    ) {
        Row(
            modifier = Modifier.clip(RoundedCornerShape(8.dp)).height(44.dp)
        ) {
            viewModel.environment.forEach { item ->
                ButtonItem(item.first, item.second == environment.value) {
                    GlobalManager.changeAdbEnv(item.second)
                }
            }
        }
    }
}

