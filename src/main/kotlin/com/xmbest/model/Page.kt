package com.xmbest.model

import androidx.compose.runtime.Composable

data class Page(val name: String, val icon: String, val comp: @Composable () -> Unit)