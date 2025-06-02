import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.xmbest.screen.Router
import com.xmbest.theme.GOOGLE_BLUE

@Composable
@Preview
fun App() {
    val lightColors = lightColors(
        // 主色调
        primary = GOOGLE_BLUE,
        // 主色调上的文本、图标色
        onPrimary = Color(0xFFEBE5E0),
        // 次色调
        secondary = Color(0xFFFAFAFA),
        // 次色调文字、图标色
        onSecondary = Color(0xCC141414),
        secondaryVariant = Color(0x15000000),
        // 页面背景
        background = Color(0xFFEFF5F9),
        // 页面展示的文本、图标色
        onBackground = Color(0xFF141414)
    )

    val darkColors =
        darkColors(
            primary = GOOGLE_BLUE,
            onPrimary = Color(0xFFEBE5E0),
            secondary = Color(0xFF111111),
            onSecondary = Color(0xCCEBE5E0),
            secondaryVariant = Color(0x15000000),
            background = Color(0xFF1B1B1B),
            onBackground = Color(0x99EBE5E0)
        )
    MaterialTheme(colors = lightColors) {
        Router()
    }
}

fun main() = application {
    val state =
        rememberWindowState(width = 1280.dp, height = 720.dp, position = WindowPosition.Aligned(Alignment.Center))
    Window(title = "EasyADB", onCloseRequest = ::exitApplication, state = state) {
        App()
    }
}