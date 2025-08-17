package com.xmbest.base

import androidx.lifecycle.ViewModel
import com.xmbest.Config
import com.xmbest.locale.PropertiesLocalization
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<T> : ViewModel() {
    protected val strings =
        PropertiesLocalization.Companion.create(Config.STRINGS_NAME, Config.locale.value)

    abstract val _uiState: MutableStateFlow<T>
    val uiState by lazy { _uiState.asStateFlow() }

    fun getString(key: String) = strings.get(key)
}