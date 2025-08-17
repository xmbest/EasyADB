package com.xmbest.ddmlib

import java.io.PrintWriter
import java.io.StringWriter
import java.util.logging.ConsoleHandler
import java.util.logging.Formatter
import java.util.logging.LogRecord
import java.util.logging.Logger
import java.text.SimpleDateFormat
import java.util.Date
import java.util.logging.Level

class ConsoleFormatter : Formatter() {

    override fun format(record: LogRecord): String {
        val message = formatMessage(record)
        var throwable = ""
        if (record.thrown != null) {
            val sw = StringWriter()
            PrintWriter(sw).use { pw ->
                pw.println()
                record.thrown.printStackTrace(pw)
            }
            throwable = "\n" + sw
        }
        val currentThread = Thread.currentThread()
        val stackTrace = currentThread.stackTrace
        val target = stackTrace.firstOrNull {
            it.className != ConsoleFormatter::class.java.name &&
            it.className != LogUtil::class.java.name &&
            !it.className.startsWith("java.util.logging") &&
            !it.className.startsWith("java.lang.Thread")
        } ?: stackTrace.getOrElse(8) { stackTrace.last() }
        val time = SimpleDateFormat("yy-MM-dd HH:mm:ss.SSS").format(Date(record.millis))
        val color = when (record.level) {
            Level.SEVERE -> ANSI_RED
            Level.WARNING -> ANSI_YELLOW
            else -> ANSI_WHITE
        }
        return "$color$time [${currentThread.name}] $message$throwable(${target.fileName}:${target.lineNumber})\n$ANSI_RESET"
    }

    companion object {
        /**
         * 将[ConsoleFormatter]实例指定为[Logger]的输出格式
         *
         * @param logger
         * @return always logger
         */
        fun attachToLogger(logger: Logger?): Logger? {
            logger?.let {
                /** 禁用原输出handler,否则会输出两次 */
                it.useParentHandlers = false
                val consoleHandler = ConsoleHandler()
                consoleHandler.formatter = ConsoleFormatter()
                it.addHandler(consoleHandler)
            }
            return logger
        }
    }
}

private const val ANSI_RESET = "\u001B[0m"
private const val ANSI_RED = "\u001B[31m"
private const val ANSI_YELLOW = "\u001B[33m"
private const val ANSI_WHITE = "\u001B[37m"