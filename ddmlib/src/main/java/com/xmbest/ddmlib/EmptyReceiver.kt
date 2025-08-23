package com.xmbest.ddmlib

import com.android.ddmlib.MultiLineReceiver

open class EmptyReceiver : MultiLineReceiver() {
    override fun processNewLines(lines: Array<out String?>?) {

    }

    override fun isCancelled() = false
}