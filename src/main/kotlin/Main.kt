import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.xmbest.Config
import com.xmbest.module.InitModule
import com.xmbest.screen.router.RouterScreen

@OptIn(InternalComposeUiApi::class)
@Composable
@Preview
fun App() {

    val isDart = Config.isDart.collectAsState()

    MaterialTheme(
        colors =
            if (isDart.value ?: isSystemInDarkTheme())
                Config.darkColors
            else
                Config.lightColors
    ) {
        RouterScreen()
    }
}

fun main() = application {
    InitModule.init()
    val windowState = Config.windowState.collectAsState()
    Window(title = "EasyADB", onCloseRequest = ::exitApplication, state = windowState.value) {
        App()
    }
}