package com.xmbest.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class Page(val name: String, val icon: ImageVector, val comp: @Composable () -> Unit)