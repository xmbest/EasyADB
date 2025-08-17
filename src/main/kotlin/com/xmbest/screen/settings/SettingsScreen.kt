package com.xmbest.screen.settings

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
import com.xmbest.component.ButtonItem
import com.xmbest.component.SettingsItem

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {

    val uiState = viewModel.uiState.collectAsState().value

    SettingsItem(
        title = viewModel.getString("theme.setting"),
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp, start = 12.dp)
    ) {
        Row(
            modifier = Modifier.clip(RoundedCornerShape(8.dp)).height(44.dp)
        ) {
            viewModel.themeList.forEach { item ->
                ButtonItem(item.first, item.second == uiState.theme) {
                    viewModel.onEvent(SettingsUiEvent.UpdateTheme(item.second))
                }
            }
        }
    }

    SettingsItem(
        title = viewModel.getString("adb.config"),
        modifier = Modifier.fillMaxWidth().padding(start = 12.dp, top = 6.dp)
    ) {
        Row(
            modifier = Modifier.clip(RoundedCornerShape(8.dp)).height(44.dp)
        ) {
            viewModel.envList.forEach { item ->
                ButtonItem(item.first, item.second.path == uiState.adbPAth) {
                    viewModel.onEvent(SettingsUiEvent.UpdateAdbEnv(item.second))
                }
            }
        }
    }
}

