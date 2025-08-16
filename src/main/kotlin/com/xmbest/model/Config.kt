package com.xmbest.model

data class Config(
    /**
     * 首页
     */
    var homeIndex: Int = 0,
    /**
     * 主题
     */
    var theme: Theme = Theme.System,

    /**
     * 自定义adb路径
     */
    var customAdbAbsolutePath: String = ""
)