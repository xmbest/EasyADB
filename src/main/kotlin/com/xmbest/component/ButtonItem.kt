package com.xmbest.component

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ButtonItem(text: String, select: Boolean, onClick: () -> Unit) {
    Button(
        onClick = {
            onClick.invoke()
        },
        shape = RoundedCornerShape(0.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor =
                if (select)
                    MaterialTheme.colors.primary
                else MaterialTheme.colors.surface
        ),
        modifier = Modifier.fillMaxHeight()
    ) {
        Text(text)
    }
}