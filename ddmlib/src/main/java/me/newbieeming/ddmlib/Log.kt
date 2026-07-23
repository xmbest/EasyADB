package me.newbieeming.ddmlib

import java.io.File
import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger


object Log {
    private const val LOG_FILE_SIZE = 1024 * 1024
    private const val LOG_FILE_COUNT = 5
    private val logger: Logger = Logger.getLogger(Log::class.java.name)
    private val logDirectory = File(System.getProperty("user.home"), ".easyAdb/log")

    init {
        logger.level = Level.FINE
        ConsoleFormatter.attachToLogger(logger)
        attachFileHandler()
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

    private fun attachFileHandler() {
        runCatching {
            check(logDirectory.exists() || logDirectory.mkdirs()) {
                "Unable to create log directory: ${logDirectory.absolutePath}"
            }
            FileHandler(
                File(logDirectory, "easyadb.%g.log").absolutePath,
                LOG_FILE_SIZE,
                LOG_FILE_COUNT,
                true,
            ).apply {
                level = Level.ALL
                formatter = ConsoleFormatter()
                logger.addHandler(this)
            }
        }.onFailure { throwable ->
            logger.log(Level.WARNING, "Unable to enable file logging", throwable)
        }
    }
}
