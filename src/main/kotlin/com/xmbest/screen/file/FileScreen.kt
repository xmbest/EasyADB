package com.xmbest.screen.file

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.xmbest.component.FileContent

@Composable
fun FileScreen() {
    Column {
        repeat(12) {
            FileContent()
        }
    }
}