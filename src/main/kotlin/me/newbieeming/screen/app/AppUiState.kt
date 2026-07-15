package me.newbieeming.screen.app

import androidx.compose.ui.graphics.vector.ImageVector
import com.android.ddmlib.IDevice
import me.newbieeming.ddmlib.AppInfo
import me.newbieeming.ddmlib.ProcessInfo

data class AppUiState(
    val filter: String = "",
    val auto: Boolean = true,
    val third: Boolean = true,
    val mode: AppShowMode = AppShowMode.ProcessMode,
    val buttonList: List<ButtonInfo> = emptyList(),
    val processList: List<ProcessInfo> = emptyList(),
    val appList: List<AppInfo> = emptyList(),
    val device: IDevice? = null
)

sealed class AppShowMode {
    object AppMode : AppShowMode()
    object ProcessMode : AppShowMode()
}

data class ButtonInfo(
    val description: String,
    val icon: ImageVector,
    val isSelected: () -> Boolean,
    val isShow: () -> Boolean,
    val onClick: () -> Unit
)