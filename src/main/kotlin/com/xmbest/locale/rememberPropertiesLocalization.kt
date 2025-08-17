package com.xmbest.locale

import androidx.compose.runtime.*
import com.xmbest.Config
import java.util.*

@Composable
fun rememberPropertiesLocalization(
    baseName: String = Config.STRINGS_NAME,
    locale: Locale = Locale.getDefault()
): PropertiesLocalization {
    return remember(baseName, locale) {
        PropertiesLocalization.create(baseName, locale)
    }
}