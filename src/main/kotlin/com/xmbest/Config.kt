package com.xmbest

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import com.xmbest.model.Theme
import com.xmbest.theme.GOOGLE_BLUE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

object Config {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    const val STRINGS_NAME = "strings"

    val lightColors = lightColors(
        // 主色调
        primary = GOOGLE_BLUE,
        // 主色调上的文本、图标色
        onPrimary = Color(0xFFEBE5E0),
        // 次色调
        secondary = Color(0xFFFAFAFA),
        // 次色调文字、图标色
        onSecondary = Color(0xCC141414), secondaryVariant = Color(0x15000000),
        // 页面背景
        background = Color(0xFFEFF5F9),
        // 页面展示的文本、图标色
        onBackground = Color(0xEE626465)
    )

    val darkColors = darkColors(
        primary = GOOGLE_BLUE,
        onPrimary = Color(0xFFEBE5E0),
        secondary = Color(0xFF2C2C2D),
        onSecondary = Color(0xCCEBE5E0),
        secondaryVariant = Color(0x15000000),
        background = Color(0xFF202021),
        onBackground = Color(0x99EBE5E0)
    )

    private val _windowState = MutableStateFlow(
        WindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = DpSize(1280.dp, 720.dp)
        )
    )

    private val _locale = MutableStateFlow(Locale.ENGLISH)
    val locale = _locale.asStateFlow()

    val isDart = MutableStateFlow<Boolean?>(null)

    val windowState = _windowState.asStateFlow()

    private val _theme = MutableStateFlow(Theme.System)

    val theme = _theme.asStateFlow()


    init {
        coroutineScope.launch {
            theme.collectLatest { t ->
                isDart.update {
                    when (t) {
                        Theme.Dark -> true
                        Theme.Light -> false
                        else -> null
                    }
                }
            }
        }
    }

    fun changeTheme(newTheme: Theme) {
        _theme.update { newTheme }
    }

}