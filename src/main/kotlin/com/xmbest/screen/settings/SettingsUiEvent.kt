package com.xmbest.screen.settings

import com.xmbest.model.Environment
import com.xmbest.model.Theme

sealed class SettingsUiEvent {
    class UpdateTheme(val theme: Theme) : SettingsUiEvent()
    class UpdateAdbEnv(val environment: Environment) : SettingsUiEvent()
}