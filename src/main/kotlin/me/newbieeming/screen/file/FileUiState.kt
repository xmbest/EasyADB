package me.newbieeming.screen.file

import com.android.ddmlib.FileListingService

data class FileUiState(
    val parentPath: String = "/sdcard",
    val children: List<FileListingService.FileEntry> = emptyList(),
    val isDragging: Boolean = false,  // 是否处于拖拽状态
    val uploadTipText: String = "", // 上传提示文本
    val toast: String = "",
    val filterStr: String = "",
    val favorites: List<String> = emptyList(), // 收藏夹列表
    val showFavoritesDropdown: Boolean = false, // 是否显示收藏夹下拉菜单
    val isSelectionMode: Boolean = false, // 是否为选中模式
    val selectedFilePaths: Set<String> = emptySet() // 选中的文件路径列表
)
