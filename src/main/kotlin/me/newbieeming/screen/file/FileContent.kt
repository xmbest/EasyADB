package me.newbieeming.screen.file

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.ddmlib.FileListingService
import me.newbieeming.LocalDialogState
import me.newbieeming.ddmlib.ClipboardUtil
import me.newbieeming.theme.CardShape
import me.newbieeming.theme.ChipShape
import me.newbieeming.theme.TextFieldShape
import me.newbieeming.util.DialogUtil
import me.newbieeming.util.InputDialogUtil

private const val FILE_NAME_AVERAGE_CHARACTER_WIDTH_DP = 9f
private const val FILE_NAME_PREFIX_RATIO = 0.42f
private const val MIN_VISIBLE_FILE_NAME_CHARACTERS = 24
private const val FILE_NAME_ELLIPSIS = "..."

@Composable
fun FileContent(
    file: FileListingService.FileEntry,
    viewModel: FileViewModel,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    isFavorite: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    var showRenameDialog by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.fillMaxWidth().padding(start = 6.dp, end = 6.dp, bottom = 6.dp)
            .clip(CardShape).background(
                if (isSelected) MaterialTheme.colors.primary.copy(alpha = 0.16f)
                else MaterialTheme.colors.surface
            )
            .hoverable(interactionSource)
            .fileInteractions(file, isSelectionMode, viewModel)
            .padding(vertical = 6.dp, horizontal = 12.dp)
    ) {
        SelectionIndicator(isSelectionMode, isSelected, viewModel)
        val fileTypeInfo = viewModel.getFileTypeInfo(file.type)
        Icon(
            imageVector = fileTypeInfo.icon,
            contentDescription = "",
            tint = Color.White,
            modifier = Modifier.clip(ChipShape).background(MaterialTheme.colors.primary)
                .padding(8.dp)
        )
        FileDetails(
            file = file,
            fileTypeInfo = fileTypeInfo,
            sizeText = viewModel.byte2Gb(file.size),
            modifier = Modifier.weight(1f)
        )

        Box(modifier = Modifier.width(176.dp)) {
            if (isHovered && !isSelectionMode) {
                FileActions(file, viewModel, isFavorite) { showRenameDialog = true }
            }
        }
    }

    if (showRenameDialog) {
        RenameFileDialog(file, viewModel) { showRenameDialog = false }
    }
}

private fun Modifier.fileInteractions(
    file: FileListingService.FileEntry,
    isSelectionMode: Boolean,
    viewModel: FileViewModel
): Modifier {
    val filePath = file.absolutePath
    val onDoubleClick = if (!isSelectionMode && file.isDirectory) {
        { viewModel.onEvent(FileUiEvent.Navigation.NavigateToPath(filePath)) }
    } else {
        null
    }
    val onLongClick = if (!isSelectionMode) {
        { viewModel.onEvent(FileUiEvent.UI.EnterSelectionMode(filePath)) }
    } else {
        null
    }

    return combinedClickable(
        onDoubleClick = onDoubleClick,
        onLongClick = onLongClick,
        onClick = {
            if (isSelectionMode) {
                viewModel.onEvent(FileUiEvent.UI.ToggleFileSelection(filePath))
            }
        }
    )
}

@Composable
private fun SelectionIndicator(
    isSelectionMode: Boolean,
    isSelected: Boolean,
    viewModel: FileViewModel
) {
    if (!isSelectionMode) return

    Icon(
        imageVector = if (isSelected) Icons.Default.CheckBox else Icons.Outlined.CheckBoxOutlineBlank,
        contentDescription = viewModel.getString(
            if (isSelected) "file.selection.unselect" else "file.selection.select"
        ),
        tint = MaterialTheme.colors.primary,
        modifier = Modifier.size(22.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FileDetails(
    file: FileListingService.FileEntry,
    fileTypeInfo: FileTypeInfo,
    sizeText: String,
    modifier: Modifier = Modifier
) {
    val fileNameStyle = TextStyle.Default.copy(fontSize = 18.sp)
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val fileNameTooltipState = rememberTooltipState()

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.Bottom) {
            BoxWithConstraints(modifier = Modifier.weight(1f)) {
                val maxFileNameWidthPx = with(density) { maxWidth.roundToPx() }
                val maxVisibleCharacters = (maxWidth.value / FILE_NAME_AVERAGE_CHARACTER_WIDTH_DP)
                    .toInt()
                    .coerceAtLeast(MIN_VISIBLE_FILE_NAME_CHARACTERS)
                val ellipsisStartIndex = (maxVisibleCharacters * FILE_NAME_PREFIX_RATIO)
                    .toInt()
                    .coerceAtLeast(1)
                val displayFileName = remember(
                    file.name,
                    maxFileNameWidthPx,
                    maxVisibleCharacters,
                    ellipsisStartIndex,
                    textMeasurer,
                ) {
                    abbreviateFileName(
                        fileName = file.name,
                        maxVisibleCharacters = maxVisibleCharacters,
                        ellipsisStartIndex = ellipsisStartIndex,
                        maxWidthPx = maxFileNameWidthPx,
                        measureTextWidth = { text ->
                            textMeasurer.measure(
                                text = text,
                                style = fileNameStyle,
                                maxLines = 1,
                            ).size.width
                        },
                    )
                }

                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                    tooltip = { Text(file.name) },
                    state = fileNameTooltipState,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = displayFileName,
                        maxLines = 1,
                        softWrap = false,
                        color = MaterialTheme.colors.onSurface,
                        style = fileNameStyle,
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Box(modifier = Modifier.width(72.dp), contentAlignment = Alignment.BottomStart) {
                Text(
                    text = sizeText,
                    maxLines = 1,
                    softWrap = false,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    style = TextStyle.Default.copy(fontSize = 12.sp),
                    modifier = Modifier.padding(bottom = 2.dp),
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            FileMetadataLabel(
                text = fileTypeInfo.text,
                textColor = MaterialTheme.colors.onPrimary,
                backgroundColor = MaterialTheme.colors.primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            FileMetadataLabel(
                text = file.permissions,
                textColor = MaterialTheme.colors.onSecondary,
                backgroundColor = MaterialTheme.colors.secondary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${file.date} ${file.time}",
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun abbreviateFileName(
    fileName: String,
    maxVisibleCharacters: Int,
    ellipsisStartIndex: Int,
    maxWidthPx: Int,
    measureTextWidth: (String) -> Int,
): String {
    if (fileName.length <= maxVisibleCharacters && measureTextWidth(fileName) <= maxWidthPx) {
        return fileName
    }

    val extensionStart = fileName.lastIndexOf('.').takeIf { it in 1 until fileName.lastIndex }
        ?: fileName.length
    val startIndex = ellipsisStartIndex.coerceIn(1, (extensionStart - 1).coerceAtLeast(1))
    if (startIndex >= extensionStart) return fileName

    val extensionLength = fileName.length - extensionStart

    val suffixLength = (maxVisibleCharacters - startIndex - FILE_NAME_ELLIPSIS.length)
        .coerceAtLeast(extensionLength)

    var ellipsisEndIndex = (fileName.length - suffixLength).coerceIn(startIndex + 1, extensionStart)
    var displayName = fileName.replaceRange(startIndex, ellipsisEndIndex, FILE_NAME_ELLIPSIS)

    while (measureTextWidth(displayName) > maxWidthPx && ellipsisEndIndex < extensionStart) {
        ellipsisEndIndex++
        displayName = fileName.replaceRange(startIndex, ellipsisEndIndex, FILE_NAME_ELLIPSIS)
    }
    return displayName
}

@Composable
private fun FileMetadataLabel(text: String, textColor: Color, backgroundColor: Color) {
    Text(
        text = text,
        color = textColor,
        style = TextStyle.Default.copy(fontSize = 14.sp),
        modifier = Modifier.clip(TextFieldShape).background(backgroundColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}

@Composable
private fun FileActions(
    file: FileListingService.FileEntry,
    viewModel: FileViewModel,
    isFavorite: Boolean,
    onRename: () -> Unit
) {
    val dialogState = LocalDialogState.current
    val favoriteLabel = viewModel.getString(if (isFavorite) "favorites.cancel" else "favorites.add")

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FileActionButton(Icons.Default.ContentCopy, viewModel.getString("file.copyPath")) {
            ClipboardUtil.setSysClipboardText(file.absolutePath)
        }
        FileActionButton(
            if (isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
            favoriteLabel
        ) {
            viewModel.onEvent(FileUiEvent.Favorites.ToggleFavorite(file.absolutePath))
        }
        FileActionButton(Icons.Default.Edit, viewModel.getString("file.rename"), onClick = onRename)
        FileActionButton(Icons.Default.FileDownload, viewModel.getString("file.export")) {
            viewModel.onEvent(FileUiEvent.FileOperation.DownloadFiles(listOf(file)))
        }
        FileActionButton(
            icon = Icons.Default.Delete,
            description = viewModel.getString("file.delete"),
            tint = MaterialTheme.colors.error
        ) {
            DialogUtil.showWarning(
                dialogState = dialogState,
                message = viewModel.getString("file.delete.confirm").format(file.absolutePath),
                onConfirm = {
                    viewModel.onEvent(FileUiEvent.FileOperation.DeleteFiles(listOf(file)))
                },
                onCancel = {}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FileActionButton(
    icon: ImageVector,
    description: String,
    tint: Color = MaterialTheme.colors.primary,
    onClick: () -> Unit
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = { Text(description) },
        state = rememberTooltipState()
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = description,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun RenameFileDialog(
    file: FileListingService.FileEntry,
    viewModel: FileViewModel,
    onDismiss: () -> Unit
) {
    InputDialogUtil.showRenameDialog(
        dialogState = LocalDialogState.current,
        title = viewModel.getString("file.rename"),
        currentName = file.name,
        onConfirm = { newName ->
            viewModel.onEvent(FileUiEvent.FileOperation.RenameFile(file.absolutePath, newName))
            onDismiss()
        },
        onCancel = onDismiss
    )
}
