package com.xmbest.model

import com.xmbest.programAdbAbsolutePath

/**
 * adb执行环境
 */
sealed class Environment(var path: String) {
    /**
     * 环境变量
     */
    object System : Environment("adb")

    /**
     * 程序自带adb环境
     */
    object Program : Environment(programAdbAbsolutePath)

    /**
     * 自定义路径
     */
    class Custom(path: String) : Environment(path)
}