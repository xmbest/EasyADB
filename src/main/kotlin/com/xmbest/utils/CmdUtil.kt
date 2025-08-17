package com.xmbest.utils

import java.io.File

object CmdUtil {

    /**
     * 启动一个进程并返回 Process 对象。
     * @param command 要执行的命令（如 "ls -l"）。
     * @return 启动的 Process 对象。
     * @throws IllegalArgumentException 如果命令为空或空白。
     */
    fun run(command: String): Process {
        require(command.isNotBlank()) { "Command must not be blank." }
        val commands = command.split(" ").filter { it.isNotBlank() }
        val builder = ProcessBuilder(commands)
        builder.directory(File(System.getProperty("user.dir")))
        return builder.start()
    }

    /**
     * 执行命令并读取标准输出。
     * @param command 要执行的命令（如 "ls -l"）。
     * @return 命令的标准输出字符串，失败时返回空字符串。
     */
    fun runAndGetResult(command: String): String {
        return runCatching {
            val process = run(command)
            val startTime = System.currentTimeMillis()
            process.waitFor()
            val endTime = System.currentTimeMillis()
            println("exec `$command` finish ${endTime - startTime} ms")
            process.inputStream.use { input ->
                input.bufferedReader().use { reader ->
                    reader.readText()
                }
            }
        }.onFailure { it.printStackTrace() }.getOrNull() ?: ""
    }
}