package me.newbieeming.util

import androidx.compose.material.Colors
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.NativeLibrary
import com.sun.jna.Pointer

object WindowsTitleBarUtil {
    private const val DWM_CAPTION_COLOR = 35
    private const val DWM_TEXT_COLOR = 36

    private val setWindowAttribute by lazy {
        NativeLibrary.getInstance("dwmapi").getFunction("DwmSetWindowAttribute")
    }

    fun apply(window: ComposeWindow, colors: Colors) {
        val hwnd = Native.getComponentPointer(window)
        // DWMWA_CAPTION_COLOR 使用 COLORREF，不支持 alpha；用 compositeOver 将透明度预先混合为实色
        val base = if (colors.isLight) Color.White else Color.Black
        val captionColor = colors.background.copy(alpha = 0.6f).compositeOver(base)
        setColor(hwnd, DWM_CAPTION_COLOR, captionColor.toWindowsColorRef())
        setColor(hwnd, DWM_TEXT_COLOR, colors.onBackground.toWindowsColorRef())
    }

    private fun setColor(hwnd: Pointer, attribute: Int, color: Int) {
        val colorValue = Memory(Int.SIZE_BYTES.toLong()).apply {
            setInt(0, color)
        }
        setWindowAttribute.invokeInt(
            arrayOf(hwnd, attribute, colorValue, Int.SIZE_BYTES),
        )
    }
}

private fun Color.toWindowsColorRef(): Int {
    val red = (red * 255).toInt() and 0xFF
    val green = (green * 255).toInt() and 0xFF
    val blue = (blue * 255).toInt() and 0xFF
    return red or (green shl 8) or (blue shl 16)
}