import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.application
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import me.newbieeming.Config
import me.newbieeming.model.Theme
import me.newbieeming.module.InitModule
import me.newbieeming.screen.loading.LoadingScreen
import me.newbieeming.screen.navigation.NaviScreen
import me.newbieeming.util.ErrorLogger
import me.newbieeming.util.PreferencesUtil
import me.newbieeming.util.PreferencesUtil.PREFERENCES_LOADING_MIN_DURATION_MS
import me.newbieeming.util.WindowsTitleBarUtil
import org.jetbrains.skiko.hostOs
import kotlin.time.Duration.Companion.milliseconds

private val macTitleBarHeight = 28.dp

@Composable
fun FrameWindowScope.App() {
    val colors = when (val theme = Config.theme.collectAsState().value) {
        Theme.System -> {
            if (isSystemInDarkTheme()) Theme.Night.color else Theme.Light.color
        }

        else -> theme.color
    }

    val titleBarAlpha = Config.titleBarAlpha.collectAsState().value

    LaunchedEffect(colors.background, colors.onBackground, colors.isLight, titleBarAlpha) {
        if (hostOs.isMacOS) {
            window.rootPane.putClientProperty(
                "apple.awt.windowAppearance",
                if (colors.isLight) "NSAppearanceNameAqua" else "NSAppearanceNameDarkAqua",
            )
        } else if (hostOs.isWindows) {
            WindowsTitleBarUtil.apply(window, colors, titleBarAlpha)
        }
    }

    var isReady by remember { mutableStateOf(false) }

    // init() 与最短等待时长并发，两者都完成才切换到主界面
    // 最短时长 = 应用本身启动耗时 + 用户在设置中配置的额外时长
    LaunchedEffect(Unit) {
        val minDurationMs = PreferencesUtil.get(PREFERENCES_LOADING_MIN_DURATION_MS, 500)
        coroutineScope {
            val initJob = async(Dispatchers.IO) { InitModule.init() }
            val delayJob = async { delay(minDurationMs.toLong().milliseconds) }
            initJob.await()
            delayJob.await()
        }
        isReady = true
    }

    MaterialTheme(colors = colors) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background.copy(alpha = titleBarAlpha)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = if (hostOs.isMacOS) macTitleBarHeight else 0.dp),
            ) {
                Crossfade(targetState = isReady) { ready ->
                    if (ready) NaviScreen() else LoadingScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
        ErrorLogger.log(throwable)
    }
    application {
        val windowState = Config.windowState.collectAsState()
        val viewModelStore = remember { ViewModelStore() }
        val viewModelStoreOwner = remember {
            object : ViewModelStoreOwner {
                override val viewModelStore: ViewModelStore = viewModelStore
            }
        }
        SwingWindow(
            title = "EasyADB",
            onCloseRequest = ::exitApplication,
            state = windowState.value,
            icon = painterResource("icon/logo.ico"),
            init = { window ->
                if (hostOs.isMacOS) {
                    window.rootPane.putClientProperty("apple.awt.fullWindowContent", true)
                    window.rootPane.putClientProperty("apple.awt.transparentTitleBar", true)
                }
            },
        ) {
            LaunchedEffect(Unit) {
                snapshotFlow { windowState.value.size }
                    .distinctUntilChanged()
                    .collect { sizeDp ->
                        println("width: ${sizeDp.width.value},height: ${sizeDp.height.value}")
                        if (Config.getWindowSizeMode() == Config.WindowSizeMode.Remember) {
                            Config.saveRememberWindowSizeDp(sizeDp)
                        }
                    }
            }

            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner
            ) {
                App()
            }
        }
    }
}
