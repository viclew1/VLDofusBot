package fr.lewon.dofus.bot.gui2.main.exploration

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import fr.lewon.dofus.bot.core.VldbCoreInitializer
import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.i18n.I18NUtil
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.min

@Composable
fun ExplorationHelperContent() {
    Box(Modifier.clip(RectangleShape).fillMaxSize()) {
        val mapDrawCellsBySubArea = ExplorationUIUtil.mapDrawCellBySubArea
        val allMaps = mapDrawCellsBySubArea.flatMap { it.value }
        val minPosX = allMaps.minOf { it.map.posX }
        val minPosY = allMaps.minOf { it.map.posY }
        val colorByMap = remember { mutableStateOf(HashMap<Double, Color>()) }
        val computeColors = {
            val newColorByMap = HashMap<Double, Color>()
            val now = System.currentTimeMillis()
            val maxExplorationAge = 2 * 3600 * 1000
            val oldestExplorationTime = now - maxExplorationAge
            for ((mapId, time) in ExplorationUIUtil.uiState.value.exploredTimeByMap) {
                val lastExploreTime = maxOf(oldestExplorationTime, time)
                val red = 255 * (now - lastExploreTime) / maxExplorationAge
                val blue = 255 - red
                newColorByMap[mapId] = Color(red.toInt(), 0, blue.toInt())
            }
            colorByMap.value = newColorByMap
        }
        computeColors()
        LaunchedEffect(Unit) {
            while (true) {
                delay(10000)
                computeColors()
            }
        }
        Canvas(Modifier.pointerInput(Unit) {
            detectDragGestures(
                onDrag = { _, dragAmount ->
                    val currentUIState = ExplorationUIUtil.uiState.value
                    ExplorationUIUtil.uiState.value = currentUIState.copy(
                        offset = currentUIState.offset + dragAmount / currentUIState.scale
                    )
                }
            )
        }.pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent()
                    if (event.type == PointerEventType.Scroll && event.changes.isNotEmpty() && event.changes.all { !it.isConsumed }) {
                        val currentUIState = ExplorationUIUtil.uiState.value
                        val delta = event.changes.sumOf { it.scrollDelta.y.toInt() }
                        ExplorationUIUtil.uiState.value = currentUIState.copy(
                            scale = min(4f, max(0.5f, currentUIState.scale * (1 - delta.toFloat() / 10)))
                        )
                    }
                }
            }
        }.fillMaxSize().align(Alignment.Center).graphicsLayer {
            scaleX = ExplorationUIUtil.uiState.value.scale
            scaleY = ExplorationUIUtil.uiState.value.scale
        }) {
            val size = Size(5f, 5f)
            for ((subArea, mapDrawCells) in mapDrawCellsBySubArea) {
                for (mapDrawCell in mapDrawCells) {
                    val map = mapDrawCell.map
                    val color = colorByMap.value[map.id] ?: Color.Red
                    val realX = map.posX - minPosX
                    val realY = map.posY - minPosY
                    val topLeft = Offset(realX * 5f, realY * 5f) + ExplorationUIUtil.uiState.value.offset
                    this.drawRect(color, topLeft, size, blendMode = BlendMode.Src)
                    this.drawRect(color, topLeft, size, style = Stroke(1f), blendMode = BlendMode.Src)
                    this.drawRect(Color.Black, topLeft, size, style = Stroke(0.1f), blendMode = BlendMode.Src)
                }
            }
            mapDrawCellsBySubArea.flatMap { it.value }.filter {
                it.leftWall || it.bottomWall || it.rightWall || it.topWall
            }.forEach { borderCell ->
                val map = borderCell.map
                val realX = map.posX - minPosX
                val realY = map.posY - minPosY
                val topLeft = Offset(realX * 5f, realY * 5f) + ExplorationUIUtil.uiState.value.offset
                val topRight = Offset((realX + 1) * 5f, realY * 5f) + ExplorationUIUtil.uiState.value.offset
                val bottomLeft = Offset(realX * 5f, (realY + 1) * 5f) + ExplorationUIUtil.uiState.value.offset
                val bottomRight = Offset((realX + 1) * 5f, (realY + 1) * 5f) + ExplorationUIUtil.uiState.value.offset
                if (borderCell.leftWall) {
                    drawLine(Color.Black, topLeft, bottomLeft, strokeWidth = 1f, blendMode = BlendMode.Src)
                }
                if (borderCell.bottomWall) {
                    drawLine(Color.Black, bottomLeft, bottomRight, strokeWidth = 1f, blendMode = BlendMode.Src)
                }
                if (borderCell.rightWall) {
                    drawLine(Color.Black, bottomRight, topRight, strokeWidth = 1f, blendMode = BlendMode.Src)
                }
                if (borderCell.topWall) {
                    drawLine(Color.Black, topRight, topLeft, strokeWidth = 1f, blendMode = BlendMode.Src)
                }
            }
        }
    }
}

fun main() {
    VldbCoreInitializer.initAll()
    D2OUtil.getObjects("SuperAreas").forEach {
        val nameId = it["nameId"]?.toString()?.toInt() ?: -1
        val name = I18NUtil.getLabel(nameId)
        println("$name - $it")
    }
    D2OUtil.getObjects("WorldMaps").forEach {
        val nameId = it["nameId"]?.toString()?.toInt() ?: -1
        val name = I18NUtil.getLabel(nameId)
        println("$name - $it")
    }
}
