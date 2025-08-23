package com.xmbest.ddmlib

sealed class InstallState(val msg: String) {
    class Success(successMsg: String) : InstallState(successMsg)
    class Error(val errorCode: String, errorMsg: String) : InstallState(errorMsg)
    object NotConnected : InstallState("device not connected")
}