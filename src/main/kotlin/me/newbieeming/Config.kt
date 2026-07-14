package me.newbieeming

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.newbieeming.locale.PropertiesLocalization
import me.newbieeming.model.Environment
import me.newbieeming.model.Theme
import me.newbieeming.theme.blue
import me.newbieeming.theme.brown
import me.newbieeming.theme.cyan
import me.newbieeming.theme.gray
import me.newbieeming.theme.green
import me.newbieeming.theme.orange
import me.newbieeming.theme.pink
import me.newbieeming.theme.purple
import me.newbieeming.theme.red
import me.newbieeming.theme.yellow
import me.newbieeming.util.PreferencesUtil
import me.newbieeming.util.PreferencesUtil.PREFERENCES_THEME
import me.newbieeming.util.PreferencesUtil.PREFERENCES_TITLE_BAR_ALPHA
import me.newbieeming.util.PreferencesUtil.PREFERENCES_WINDOW_HEIGHT_DP
import me.newbieeming.util.PreferencesUtil.PREFERENCES_WINDOW_REMEMBER_HEIGHT_DP
import me.newbieeming.util.PreferencesUtil.PREFERENCES_WINDOW_REMEMBER_WIDTH_DP
import me.newbieeming.util.PreferencesUtil.PREFERENCES_WINDOW_SIZE_MODE
import me.newbieeming.util.PreferencesUtil.PREFERENCES_WINDOW_WIDTH_DP
import java.util.*
import kotlin.math.roundToInt

object Config {
    const val STRINGS_NAME = "strings"
    val buildVersion: String by lazy {
        System.getProperty("build_version")?.ifBlank { "unknown" } ?: "unknown"
    }
    enum class WindowSizeMode {
        Follow,
        Remember,
        Custom
    }

    private val _locale = MutableStateFlow(Locale.CHINA)
    val locale = _locale.asStateFlow()

    private val strings = PropertiesLocalization.create(STRINGS_NAME)

    val themeList = listOf(
        Theme.System,
        Theme.Light,
        Theme.Night,
        Theme.Other(strings.get("theme.red"), red),
        Theme.Other(strings.get("theme.orange"), orange),
        Theme.Other(strings.get("theme.yellow"), yellow),
        Theme.Other(strings.get("theme.green"), green),
        Theme.Other(strings.get("theme.cyan"), cyan),
        Theme.Other(strings.get("theme.blue"), blue),
        Theme.Other(strings.get("theme.purple"), purple),
        Theme.Other(strings.get("theme.pink"), pink),
        Theme.Other(strings.get("theme.brown"), brown),
        Theme.Other(strings.get("theme.gray"), gray)
    )

    val envList = listOf(
        Pair(strings.get("env.system"), Environment.System),
        Pair(strings.get("env.program"), Environment.Program),
        Pair(strings.get("env.custom"), Environment.Custom)
    )

    val defaultWindowSizeDp = DpSize(1280.dp, 720.dp)

    private val _windowState = MutableStateFlow(
        WindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = resolveWindowSizeDp()
        )
    )

    val windowState = _windowState.asStateFlow()

    private val currentTheme = themeList.firstOrNull {
        it.label == PreferencesUtil.get(PREFERENCES_THEME, Theme.System.label)
    } ?: Theme.System

    private val _theme = MutableStateFlow(currentTheme)

    val theme = _theme.asStateFlow()

    fun changeTheme(newTheme: Theme) {
        _theme.update { newTheme }
    }

    private val _titleBarAlpha = MutableStateFlow(
        PreferencesUtil.get(PREFERENCES_TITLE_BAR_ALPHA, 0.8f)
    )

    val titleBarAlpha = _titleBarAlpha.asStateFlow()

    fun setTitleBarAlpha(alpha: Float) {
        val clamped = alpha.coerceIn(0f, 1f)
        PreferencesUtil.set(PREFERENCES_TITLE_BAR_ALPHA, clamped)
        _titleBarAlpha.update { clamped }
    }

    fun getWindowSizeMode(): WindowSizeMode {
        val modeName = PreferencesUtil.get(PREFERENCES_WINDOW_SIZE_MODE, WindowSizeMode.Follow.name)
        return WindowSizeMode.entries.firstOrNull { it.name == modeName } ?: WindowSizeMode.Follow
    }

    fun setWindowSizeMode(mode: WindowSizeMode) {
        PreferencesUtil.set(PREFERENCES_WINDOW_SIZE_MODE, mode.name)
    }

    fun getCustomWindowSizeDp(): DpSize {
        val width =
            PreferencesUtil.get(PREFERENCES_WINDOW_WIDTH_DP, defaultWindowSizeDp.width.value)
        val height =
            PreferencesUtil.get(PREFERENCES_WINDOW_HEIGHT_DP, defaultWindowSizeDp.height.value)
        return DpSize(width.dp, height.dp)
    }

    fun saveCustomWindowSizeDp(size: DpSize) {
        PreferencesUtil.set(PREFERENCES_WINDOW_WIDTH_DP, size.width.value.roundToInt())
        PreferencesUtil.set(PREFERENCES_WINDOW_HEIGHT_DP, size.height.value.roundToInt())
    }

    private fun getRememberWindowSizeDp(): DpSize {
        val width =
            PreferencesUtil.get(PREFERENCES_WINDOW_REMEMBER_WIDTH_DP, defaultWindowSizeDp.width.value)
        val height =
            PreferencesUtil.get(PREFERENCES_WINDOW_REMEMBER_HEIGHT_DP, defaultWindowSizeDp.height.value)
        return DpSize(width.dp, height.dp)
    }

    fun saveRememberWindowSizeDp(size: DpSize) {
        PreferencesUtil.set(PREFERENCES_WINDOW_REMEMBER_WIDTH_DP, size.width.value.roundToInt())
        PreferencesUtil.set(PREFERENCES_WINDOW_REMEMBER_HEIGHT_DP, size.height.value.roundToInt())
    }

    private fun resolveWindowSizeDp(): DpSize {
        return when (getWindowSizeMode()) {
            WindowSizeMode.Follow -> defaultWindowSizeDp
            WindowSizeMode.Remember -> getRememberWindowSizeDp()
            WindowSizeMode.Custom -> getCustomWindowSizeDp()
        }
    }

    fun updateWindowSize(size: DpSize) {
        _windowState.value.size = size
    }
}
