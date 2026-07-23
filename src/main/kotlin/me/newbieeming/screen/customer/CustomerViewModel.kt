package me.newbieeming.screen.customer

import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.newbieeming.appStorageAbsolutePath
import me.newbieeming.base.BaseViewModel
import me.newbieeming.cfg
import me.newbieeming.ddmlib.DeviceOperate
import me.newbieeming.ddmlib.Log
import me.newbieeming.screen.customer.entity.*
import java.io.File

class CustomerViewModel : BaseViewModel<CustomerUiState>() {

    companion object {

        private const val TAG = "CustomerViewModel"

        // 类型映射
        private val typeMap = mapOf(
            FastBroadType.INPUT_SEND to InputSendData::class.java,
            FastBroadType.BUTTON_GROUP to ButtonGroupData::class.java,
            FastBroadType.SHELL_SEND to ShellSendData::class.java
        )

        private const val ADB_PREFIX = "adb "
        private const val ADB_SHELL_PREFIX = "adb shell "
        private val adbPushPrefix = Regex("""^adb\s+push(?:\s|$)""")
        private val adbPushCommand = Regex("""^adb\s+push\s+(.+?)\s+(\S+)\s*$""")
    }

    override val _uiState: MutableStateFlow<CustomerUiState> = MutableStateFlow(CustomerUiState())

    private sealed class ExecutableCommand {

        data class AdbPush(
            val localPath: String,
            val remotePath: String,
        ) : ExecutableCommand()

        data class DeviceShell(
            val command: String,
        ) : ExecutableCommand()
    }

    init {
        loadConfig()
    }

    fun onEvent(event: CustomerUiEvent) {
        viewModelScope.launch(Dispatchers.Default) {
            when (event) {
                is CustomerUiEvent.Config -> handleConfigEvent(event)
                is CustomerUiEvent.Command -> handleCommandEvent(event)
                is CustomerUiEvent.UI -> handleUIEvent(event)
            }
        }
    }

    private suspend fun handleConfigEvent(event: CustomerUiEvent.Config) {
        when (event) {
            is CustomerUiEvent.Config.Refresh -> loadConfig()
            is CustomerUiEvent.Config.Export -> handleExportConfig()
            is CustomerUiEvent.Config.Import -> handleImportConfig()
        }
    }

    private suspend fun handleCommandEvent(event: CustomerUiEvent.Command) {
        when (event) {
            is CustomerUiEvent.Command.Execute -> handleExecuteCommand(event.cmd)
        }
    }

    private fun handleUIEvent(event: CustomerUiEvent.UI) {
        when (event) {
            is CustomerUiEvent.UI.UpdateInputValue -> handleUpdateInputValue(
                event.uuid,
                event.value
            )

            is CustomerUiEvent.UI.Toast -> handleToast(event.message)

        }
    }

    private fun loadConfig() {
        var toastMessage = ""
        val configList = runCatching {
            val gson = Gson()
            val configFile = File(appStorageAbsolutePath, cfg.second)
            if (!configFile.exists()) {
                return@runCatching getDefaultConfig()
            }

            val configJson = configFile.readText()
            val configList = mutableListOf<BaseFastBroadData>()
            Log.d(TAG, "configJson = $configJson")
            val listMap: List<Map<String, Any>> =
                gson.fromJson(configJson, object : TypeToken<List<Map<String, Any>>>() {}.type)
            listMap.forEach {
                configList.add(
                    gson.fromJson(
                        gson.toJson(it),
                        typeMap[it["type"]]
                    )
                )
            }
            if (configList.isEmpty()) {
                toastMessage = getString("customer.config.empty")
            }
            configList
        }.onFailure {
            Log.e(TAG, "Error: ${it.message}")
            toastMessage = getString("customer.config.load.failed")
        }.getOrNull() ?: getDefaultConfig()

        _uiState.update { it.copy(configList = configList, toast = toastMessage) }
        Log.i(TAG, "loadConfig finished, toast: $toastMessage")
    }

    private suspend fun handleExportConfig() {
        withContext(Dispatchers.IO) {
            try {
                val configFile = File(appStorageAbsolutePath, cfg.second)
                if (!configFile.exists()) {
                    _uiState.update { it.copy(toast = getString("customer.export.no.config")) }
                    return@withContext
                }

                val file = FileKit.openFileSaver(
                    extension = "json",
                    suggestedName = "config"
                )

                if (file != null) {
                    val configContent = configFile.readText()
                    File(file.path).writeText(configContent)
                    _uiState.update { it.copy(toast = getString("customer.export.success")) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(toast = "${getString("customer.export.failed")}: ${e.message}") }
            }
        }
    }

    private suspend fun handleImportConfig() {
        withContext(Dispatchers.IO) {
            try {
                val file = FileKit.openFilePicker(
                    title = getString("customer.import.title")
                ) ?: return@withContext

                val configJson = File(file.path).readText()

                // Validate and parse config
                val validationResult = validateConfigJson(configJson)
                if (!validationResult.isValid) {
                    _uiState.update { it.copy(toast = validationResult.errorMessage!!) }
                    return@withContext
                }

                saveConfig(configJson)
                _uiState.update { it.copy(toast = getString("customer.import.success")) }

            } catch (e: Exception) {
                Log.e(TAG, "Import config failed: ${e.message}")
                _uiState.update { it.copy(toast = "${getString("customer.import.failed")}: ${e.message}") }
            }
        }
    }

    private fun validateConfigJson(configJson: String): ValidationResult {
        return try {
            val gson = Gson()
            val listMap: List<Map<String, Any>> =
                gson.fromJson(configJson, object : TypeToken<List<Map<String, Any>>>() {}.type)

            if (listMap.isEmpty()) {
                return ValidationResult(false, getString("customer.import.empty"))
            }

            // Validate each item's type and structure
            val parsedList = listMap.mapIndexed { index, itemMap ->
                val type = itemMap["type"]
                val dataClass = typeMap[type]
                    ?: return ValidationResult(false, getString("customer.import.invalid.type"))

                try {
                    gson.fromJson(gson.toJson(itemMap), dataClass)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to parse item at index $index: ${e.message}")
                    return ValidationResult(false, getString("customer.import.invalid.format"))
                }
            }

            if (parsedList.isEmpty()) {
                ValidationResult(false, getString("customer.import.empty"))
            } else {
                ValidationResult(true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Config validation failed: ${e.message}")
            ValidationResult(false, getString("customer.import.invalid.format"))
        }
    }

    private data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null,
    )

    private fun saveConfig(str: String) {
        val configFile = File(appStorageAbsolutePath, cfg.second)
        configFile.writeText(str)
        loadConfig()
    }

    private suspend fun handleExecuteCommand(cmd: String) {
        withContext(Dispatchers.IO) {
            val command = cmd.trim()

            try {
                Log.i(TAG, "Execute custom command: $command")
                executeCustomCommand(
                    parseAdbPushCommand(command)
                        ?: ExecutableCommand.DeviceShell(toDeviceShellCommand(command))
                )
            } catch (e: Exception) {
                handleCommandFailure(command, e)
            }
        }
    }

    private fun parseAdbPushCommand(command: String): ExecutableCommand.AdbPush? {
        if (!adbPushPrefix.containsMatchIn(command)) return null

        val match = adbPushCommand.matchEntire(command)
            ?: throw IllegalArgumentException("Unsupported adb push command: $command")
        return ExecutableCommand.AdbPush(
            localPath = match.groupValues[1].trim().removeSurrounding("\""),
            remotePath = match.groupValues[2].trim().removeSurrounding("\""),
        )
    }

    private fun toDeviceShellCommand(command: String): String {
        return command
            .removePrefix(ADB_SHELL_PREFIX)
            .removePrefix(ADB_PREFIX)
            .trimStart()
    }

    private fun executeCustomCommand(command: ExecutableCommand) {
        when (command) {
            is ExecutableCommand.AdbPush -> {
                DeviceOperate.pushFile(command.localPath, command.remotePath)
                Log.i(
                    TAG,
                    "Custom adb push completed: ${command.localPath} -> ${command.remotePath}"
                )
            }

            is ExecutableCommand.DeviceShell -> {
                Log.i(TAG, "Execute device shell command: ${command.command}")
                val result = DeviceOperate.executeShellCommand(command.command)
                Log.i(
                    TAG,
                    "Device shell completed: exitCode=${result.exitCode}, output=${result.output.ifBlank { "<empty>" }}",
                )
                check(result.exitCode == 0) {
                    "Device shell failed with exit code ${result.exitCode}: ${result.output}"
                }
            }
        }
    }

    private fun handleCommandFailure(command: String, error: Exception) {
        Log.e(TAG, "Custom command failed: $command", error)
        _uiState.update { it.copy(toast = "${getString("customer.command.failed")}: ${error.message}") }
    }

    private fun handleUpdateInputValue(uuid: String?, value: String) {

    }

    private fun handleToast(message: String) {
        _uiState.update { it.copy(toast = message) }
    }

    private fun getDefaultConfig(): List<BaseFastBroadData> {
        return listOf(
            InputSendData(
                title = getString("customer.default.textInput.title"),
                cmd = "input keyboard text \"{action}\"",
                template = "{action}",
                hint = getString("customer.default.textInput.hint"),
                btnText = getString("customer.default.textInput.btn")
            ),
            ButtonGroupData(
                title = getString("customer.default.systemSettings.title"),
                list = listOf(
                    ButtonData(
                        btnText = getString("customer.default.systemSettings.home"),
                        cmd = "adb shell am start com.android.settings/com.android.settings.Settings"
                    ),
                    ButtonData(
                        btnText = getString("customer.default.systemSettings.wifi"),
                        cmd = "adb shell am start -a android.settings.WIFI_SETTINGS"
                    ),
                    ButtonData(
                        btnText = getString("customer.default.systemSettings.bluetooth"),
                        cmd = "adb shell am start -a android.settings.BLUETOOTH_SETTINGS"
                    ),
                    ButtonData(
                        btnText = getString("customer.default.systemSettings.notification"),
                        cmd = "adb shell am start -a android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"
                    ),
                    ButtonData(
                        btnText = getString("customer.default.systemSettings.accessibility"),
                        cmd = "adb shell am start -a android.settings.ACCESSIBILITY_SETTINGS"
                    )
                )
            ),
            ButtonGroupData(
                title = getString("customer.default.displaySettings.title"),
                list = listOf(
                    ButtonData(
                        btnText = getString("customer.default.displaySettings.pointerOn"),
                        cmd = "adb shell settings put system pointer_location 1"
                    ),
                    ButtonData(
                        btnText = getString("customer.default.displaySettings.pointerOff"),
                        cmd = "adb shell settings put system pointer_location 0"
                    ),
                    ButtonData(
                        btnText = getString("customer.default.displaySettings.touchOn"),
                        cmd = "adb shell settings put system show_touches 1"
                    ),
                    ButtonData(
                        btnText = getString("customer.default.displaySettings.touchOff"),
                        cmd = "adb shell settings put system show_touches 0"
                    ),
                )
            ),
            ShellSendData(
                title = getString("customer.default.shell.title"),
                btnText = getString("customer.default.shell.btn"),
                hintText = getString("customer.default.shell.hint"),
                minHeight = 270
            )
        )
    }
}
