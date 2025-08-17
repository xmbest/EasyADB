package com.xmbest.ddmlib

import java.util.logging.Level
import java.util.logging.Logger


object LogUtil {
    private val logger: Logger = Logger.getLogger(LogUtil::class.java.name)

    init {
        logger.level = Level.FINE
        ConsoleFormatter.attachToLogger(logger)
    }

    fun e(
        tag: String,
        message: String,
        throwable: Throwable? = null
    ) {
        if (throwable != null) {
            logger.log(Level.SEVERE, "ERROR: [$tag] $message", throwable)
        } else {
            logger.severe("ERROR: [$tag] $message")
        }
    }

    fun d(tag: String, message: String) {
        logger.info("DEBUG: [$tag] $message")
    }

    fun i(tag: String, message: String) {
        logger.info("INFO: [$tag] $message")
    }

    fun w(tag: String, message: String) {
        logger.warning("WARN: [$tag] $message")
    }
}