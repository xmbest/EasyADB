package com.xmbest.screen.settings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xmbest.component.TitleContent

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {

    val uiState = viewModel.uiState.collectAsState().value

    TitleContent(
        title = viewModel.getString("theme.setting"),
        modifier = Modifier.fillMaxWidth().padding(start = 6.dp, top = 6.dp)
    ) {
        Row {
            viewModel.themeList.forEach { item ->
                FilterChip(
                    selected = item.second == uiState.theme,
                    onClick = { viewModel.onEvent(SettingsUiEvent.UpdateTheme(item.second)) },
                    content = { Text(item.first) },
                    colors = ChipDefaults.filterChipColors(
                        selectedContentColor = MaterialTheme.colors.onPrimary,
                        selectedBackgroundColor = MaterialTheme.colors.primary,
                        contentColor = MaterialTheme.colors.onSurface,
                        backgroundColor = MaterialTheme.colors.surface
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }

    TitleContent(
        title = viewModel.getString("adb.config"),
        modifier = Modifier.fillMaxWidth().padding(start = 6.dp, top = 6.dp)
    ) {
        Row{
            viewModel.envList.forEach { item ->
                FilterChip(
                    selected = item.second.path == uiState.adbPAth,
                    onClick = { viewModel.onEvent(SettingsUiEvent.UpdateAdbEnv(item.second)) },
                    content = { Text(item.first) },
                    colors = ChipDefaults.filterChipColors(
                        selectedContentColor = MaterialTheme.colors.onPrimary,
                        selectedBackgroundColor = MaterialTheme.colors.primary,
                        contentColor = MaterialTheme.colors.onSurface,
                        backgroundColor = MaterialTheme.colors.surface
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

