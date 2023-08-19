package fr.lewon.dofus.bot.gui.main.treasure

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.lewon.dofus.bot.composeWindow
import fr.lewon.dofus.bot.gui.custom.*
import fr.lewon.dofus.bot.gui.main.TooltipTarget
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.model.hint.GfxIdsByPoiLabel
import fr.lewon.dofus.bot.util.filemanagers.impl.TreasureHintManager
import java.awt.Point
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.*
import java.io.File

private val hintsFile: MutableState<File?> = mutableStateOf(null)
private val dropBounds = mutableStateOf(Rect.Zero)
private val dragPosition = mutableStateOf<Point?>(null)
private val toAddGfxIdsByPoiLabel = mutableStateOf<GfxIdsByPoiLabel?>(null)

@Composable
fun HintsImportContent() {
    LaunchedEffect(Unit) {
        resetHintFile()
        initDropArea()
    }
    val importing = remember {
        mutableStateOf(false)
    }
    Box {
        val isButtonHovered = remember { mutableStateOf(false) }
        AnimatedVisibility(
            importing.value,
            enter = expandIn(expandFrom = Alignment.TopEnd),
            exit = shrinkOut(shrinkTowards = Alignment.TopEnd)
        ) {
            Row(Modifier.fillMaxSize()) {
                Row(Modifier.fillMaxHeight().fillMaxSize().weight(1f)) {
                    DropFileArea()
                }
                CustomStyledColumn(headerContent = {
                    Row(Modifier.height(60.dp)) {
                        val changesCount = toAddGfxIdsByPoiLabel.value?.values?.sumOf { it.size }
                        CommonText(
                            "Added hints : ${changesCount?.toString() ?: "/"}",
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.align(Alignment.CenterVertically).padding(start = 10.dp)
                        )
                    }
                }, modifier = Modifier.width(300.dp)) {
                    toAddGfxIdsByPoiLabel.value?.let { gfxIdsByPoiLabel ->
                        HintsScrollableBox(gfxIdsByPoiLabel.mapValues { it.value.toList() }, "") { hintName, gfxId ->
                            AcceptHintOverlay(hintName, gfxId)
                        }
                    }
                }
            }
        }
        Row(Modifier.height(60.dp).align(Alignment.TopEnd)) {
            val changesCount = toAddGfxIdsByPoiLabel.value?.values?.sumOf { it.size } ?: 0
            if (importing.value && changesCount > 0) {
                Row(Modifier.height(60.dp).padding(end = 5.dp)) {
                    ButtonWithTooltip(
                        { acceptAll() },
                        "Accept all",
                        Icons.Default.Check,
                        RectangleShape,
                        width = 60.dp,
                        iconColor = Color.White,
                        defaultBackgroundColor = AppColors.GREEN.copy(alpha = 0.8f),
                        hoverBackgroundColor = AppColors.GREEN,
                        delayMillis = 0
                    )
                }
            }
            ButtonWithTooltip(
                {
                    resetHintFile()
                    importing.value = !importing.value
                },
                title = if (importing.value) "Close import" else "Import hint store",
                imageVector = if (importing.value) Icons.Default.Close else Icons.Default.Download,
                shape = RectangleShape,
                hoverBackgroundColor = AppColors.primaryLightColor,
                iconColor = if (isButtonHovered.value) Color.Black else Color.White,
                isHovered = isButtonHovered,
                hoverAnimation = false,
                width = 60.dp,
                delayMillis = 0
            )
        }
    }
}

@Composable
private fun BoxScope.AcceptHintOverlay(hintName: String, gfxId: Int) {
    Row(Modifier.align(Alignment.BottomEnd).padding(2.dp)) {
        Row(Modifier.size(25.dp)) {
            IconButton(
                { removeToAddHint(hintName, gfxId) },
                "Reject",
                AppColors.RED,
                Icons.Default.Close,
                Modifier.width(25.dp)
            )
        }
    }
}

private fun acceptAll() {
    val storeToImport = toAddGfxIdsByPoiLabel.value?.mapValues { HashSet(it.value) } ?: emptyMap()
    TreasureHintManager.addStore(storeToImport)
    toAddGfxIdsByPoiLabel.value = null
    TreasureHuntUiUtil.refreshRegisteredHints()
}

private fun removeToAddHint(hintName: String, gfxId: Int) {
    toAddGfxIdsByPoiLabel.value = toAddGfxIdsByPoiLabel.value?.let { GfxIdsByPoiLabel(it) }?.also {
        it[hintName]?.remove(gfxId)
        if (it[hintName]?.isEmpty() == true) {
            it.remove(hintName)
        }
    }
    TreasureHuntUiUtil.refreshRegisteredHints()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun IconButton(
    onClick: () -> Unit,
    title: String,
    color: Color,
    imageVector: ImageVector,
    modifier: Modifier = Modifier
) {
    val isHovered = remember { mutableStateOf(false) }
    val borderColor = if (isHovered.value) color else Color.Transparent
    val backgroundColorAlpha = if (isHovered.value) 0.4f else 0.2f
    val iconColorAlpha = if (isHovered.value) 0.5f else 0.3f
    TooltipTarget(title) {
        Image(
            imageVector,
            "",
            modifier = modifier.fillMaxSize()
                .background(color.copy(alpha = backgroundColorAlpha))
                .defaultHoverManager(isHovered)
                .border(BorderStroke(2.dp, borderColor))
                .handPointerIcon()
                .onClick { onClick() },
            colorFilter = ColorFilter.tint(Color.White.copy(alpha = iconColorAlpha))
        )
    }
}

private fun initDropArea() {
    composeWindow.contentPane.dropTarget = object : DropTarget() {

        override fun dragOver(dtde: DropTargetDragEvent?) {
            dragPosition.value = dtde?.location
        }

        override fun dragExit(dte: DropTargetEvent?) {
            dragPosition.value = null
        }

        @Synchronized
        override fun drop(evt: DropTargetDropEvent) {
            try {
                val offset = Offset(evt.location.x.toFloat(), evt.location.y.toFloat())
                if (dropBounds.value.contains(offset)) {
                    evt.acceptDrop(DnDConstants.ACTION_REFERENCE)
                    val droppedFiles = evt.transferable.getTransferData(
                        DataFlavor.javaFileListFlavor
                    ) as List<*>
                    hintsFile.value = droppedFiles.firstOrNull() as File?
                    updateGfxIdsByLabel()
                } else {
                    evt.rejectDrop()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            } finally {
                dragPosition.value = null
            }
        }
    }
}

private fun resetHintFile() {
    hintsFile.value = null
    updateGfxIdsByLabel()
}

private fun updateGfxIdsByLabel() {
    try {
        toAddGfxIdsByPoiLabel.value = hintsFile.value?.let {
            val currentStore = TreasureHintManager.getGfxIdsByPoiLabel()
            val toImportStore = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue<GfxIdsByPoiLabel>(it)
            currentStore.entries.forEach { entry ->
                toImportStore[entry.key]?.removeAll(entry.value)
            }
            GfxIdsByPoiLabel(toImportStore.filter { entry -> entry.value.isNotEmpty() })
        }
    } catch (e: Exception) {
        toAddGfxIdsByPoiLabel.value = null
    }
}

@Composable
private fun DropFileArea() {
    val dragOffset = dragPosition.value?.let {
        Offset(it.x.toFloat(), it.y.toFloat())
    }
    val modifier = if (dragOffset != null && dropBounds.value.contains(dragOffset)) {
        Modifier.background(AppColors.primaryColor)
    } else Modifier
    Box(
        modifier.fillMaxSize().onGloballyPositioned { dropBounds.value = it.boundsInWindow() }.padding(5.dp)
            .darkGrayBoxStyle()
    ) {
        Column(Modifier.align(Alignment.Center)) {
            CommonText(
                "Drop file here",
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            CommonText(
                "Current file :",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            CommonText(
                hintsFile.value?.absolutePath ?: "NONE",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}