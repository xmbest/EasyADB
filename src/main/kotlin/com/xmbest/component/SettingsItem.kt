package com.xmbest.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsItem(title: String, modifier: Modifier, comp: @Composable () -> Unit) {
    Column(modifier) {
        Text(title, color = MaterialTheme.colors.onBackground)
        Spacer(modifier = Modifier.height(6.dp))
        Row {
            comp()
        }
    }
}