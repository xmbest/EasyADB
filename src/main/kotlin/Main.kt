import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.xmbest.module.InitModule
import com.xmbest.screen.Router
import com.xmbest.viewmodel.MainViewModel

@OptIn(InternalComposeUiApi::class)
@Composable
@Preview
fun App(viewModel: MainViewModel) {
    val isDart = viewModel.isDart.collectAsState()
    MaterialTheme(
        colors =
            if (isDart.value ?: isSystemInDarkTheme())
                viewModel.darkColors
            else
                viewModel.lightColors
    ) {
        Router()
    }
}

fun main() = application {
    InitModule.init()
    val viewModel = remember { MainViewModel() }
    val windowState = viewModel.windowState.collectAsState()
    Window(title = "EasyADB", onCloseRequest = ::exitApplication, state = windowState.value) {
        App(viewModel)
    }
}