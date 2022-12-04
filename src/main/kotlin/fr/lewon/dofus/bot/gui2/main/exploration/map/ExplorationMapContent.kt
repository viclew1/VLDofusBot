package fr.lewon.dofus.bot.gui2.main.exploration.map

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isOutOfBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import fr.lewon.dofus.bot.gui2.custom.CommonText
import fr.lewon.dofus.bot.gui2.custom.defaultHoverManager
import fr.lewon.dofus.bot.gui2.main.exploration.ExplorationUIUtil
import fr.lewon.dofus.bot.gui2.main.exploration.map.helper.MapDrawCell
import fr.lewon.dofus.bot.gui2.main.exploration.map.helper.WorldMapHelperOverlay
import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharacterActivityState
import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui2.util.AppColors
import fr.lewon.dofus.bot.util.filemanagers.impl.BreedAssetManager
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private val cap = StrokeCap.Square
private val blendMode = BlendMode.Src
private val colorByMapId = mutableStateOf(HashMap<Double, Color>())

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExplorationMapContent() {
    val mapAvailableSize = remember { mutableStateOf(Size.Zero) }
    Box(Modifier.fillMaxSize().clip(RectangleShape).pointerInput(Unit) {
        detectDragGestures(
            onDrag = { _, dragAmount ->
                calculateOffset(dragAmount, mapAvailableSize.value)
            }
        )
    }.pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                // Disabled zoom while it's bugged
                if (false && event.type == PointerEventType.Scroll && event.changes.isNotEmpty() && event.changes.all { !it.isConsumed }) {
                    val currentUIState = ExplorationUIUtil.mapUIState.value
                    val delta = event.changes.sumOf { it.scrollDelta.y.toInt() }
                    ExplorationUIUtil.mapUIState.value = currentUIState.copy(
                        scale = min(
                            ExplorationUIUtil.MAX_ZOOM,
                            max(ExplorationUIUtil.MIN_ZOOM, currentUIState.scale - delta.toFloat())
                        )
                    )
                    calculateOffset(Offset.Zero, mapAvailableSize.value)
                }
            }
        }
    }.onClick {
        ExplorationUIUtil.mapUIState.value = ExplorationUIUtil.mapUIState.value.copy(selectedSubAreaId = null)
    }.onGloballyPositioned {
        mapAvailableSize.value = it.size.toSize()
        calculateOffset(Offset.Zero, it.size.toSize())
    }) {
        val computeColors = {
            val newColorByMap = HashMap<Double, Color>()
            val now = System.currentTimeMillis()
            val maxExplorationAge = 2 * 3600 * 1000
            val oldestExplorationTime = now - maxExplorationAge
            for ((mapId, time) in ExplorationUIUtil.exploredTimeByMap.value) {
                val lastExploreTime = maxOf(oldestExplorationTime, time)
                val red = 255 * (now - lastExploreTime) / maxExplorationAge
                val blue = 255 - red
                newColorByMap[mapId] = Color(red.toInt(), 0, blue.toInt())
            }
            colorByMapId.value = newColorByMap
        }
        computeColors()
        LaunchedEffect(Unit) {
            while (true) {
                delay(10000)
                computeColors()
            }
        }
        val scale = ExplorationUIUtil.mapUIState.value.scale
        Box(
            Modifier.offset(
                ExplorationUIUtil.mapUIState.value.offset.x.dp,
                ExplorationUIUtil.mapUIState.value.offset.y.dp
            ).requiredSize(ExplorationUIUtil.totalWidth.dp * scale, ExplorationUIUtil.totalHeight.dp * scale)
        ) {
            Box(
                Modifier.scale(scale).defaultHoverManager(onExit = {
                    ExplorationUIUtil.mapUIState.value =
                        ExplorationUIUtil.mapUIState.value.copy(hoveredMapDrawCell = null)
                })
            ) {
                Row(Modifier.fillMaxSize().defaultHoverManager(onHover = {
                    ExplorationUIUtil.mapUIState.value =
                        ExplorationUIUtil.mapUIState.value.copy(hoveredMapDrawCell = null)
                })) {}
                OverlayContent(WorldMapHelperOverlay)
                CellsContent()
                OverlayContent(ExplorationUIUtil.worldMapHelper.value.mapOverlayPainter)
            }
            Box(Modifier.scale(scale)) {
                SelectedSubAreaOverlay()
                CharactersContent()
            }
            Box {
                PositionTooltip()
            }
        }
    }
}

@Composable
private fun OverlayContent(painter: Painter) {
    Image(
        painter,
        "",
        modifier = Modifier.requiredSize(ExplorationUIUtil.totalWidth.dp, ExplorationUIUtil.totalHeight.dp),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun CellsContent() {
    Canvas(Modifier.fillMaxSize().pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val pass = PointerEventPass.Initial
                val event = awaitPointerEvent(pass)
                val isOutsideRelease = event.type == PointerEventType.Release &&
                        event.changes[0].isOutOfBounds(size, Size.Zero)
                if (event.type != PointerEventType.Exit && !isOutsideRelease) {
                    ExplorationUIUtil.mapUIState.value = ExplorationUIUtil.mapUIState.value.copy(
                        hoveredMapDrawCell = getMapDrawCell(event.changes[0].position)
                    )
                }
            }
        }
    }.pointerInput(Unit) {
        detectTapGestures {
            ExplorationUIUtil.mapUIState.value = ExplorationUIUtil.mapUIState.value.copy(
                selectedSubAreaId = getMapDrawCell(it)?.subAreaId
            )
        }
    }) {
        for (mapDrawCell in ExplorationUIUtil.worldMapHelper.value.priorityMapDrawCells) {
            drawCell(mapDrawCell, Fill, getColor(mapDrawCell.mapId))
        }
    }
}

private fun getMapDrawCell(position: Offset): MapDrawCell? =
    ExplorationUIUtil.worldMapHelper.value.getPriorityMapDrawCell(
        ExplorationUIUtil.minPosX + (position.x / ExplorationUIUtil.CELL_SIZE).toInt(),
        ExplorationUIUtil.minPosY + (position.y / ExplorationUIUtil.CELL_SIZE).toInt(),
    )

private fun getColor(mapId: Double): Color = colorByMapId.value[mapId] ?: Color.Red

@Composable
private fun SelectedSubAreaOverlay() {
    Canvas(Modifier.fillMaxSize()) {
        ExplorationUIUtil.mapUIState.value.selectedSubAreaId?.let { selectedSubAreaId ->
            drawSubArea(selectedSubAreaId, AppColors.primaryDarkColor, 1.8f)
        }
        ExplorationUIUtil.mapUIState.value.hoveredMapDrawCell?.let { hoveredMapDrawCell ->
            drawSubArea(hoveredMapDrawCell.subAreaId, AppColors.primaryColor, 1.5f)
            drawCell(hoveredMapDrawCell, Stroke(1f), AppColors.primaryDarkColor)
        }
    }
}

@Composable
private fun CharactersContent() {
    Box(Modifier.fillMaxSize()) {
        val worldMapHelper = ExplorationUIUtil.worldMapHelper.value
        val connectedCharactersUIStates = CharactersUIUtil.getAllCharacterUIStates().map { it.value }
            .filter { it.activityState != CharacterActivityState.DISCONNECTED && it.activityState != CharacterActivityState.TO_INITIALIZE }
        for (characterUiState in connectedCharactersUIStates) {
            val map = characterUiState.currentMap
            if (map != null) {
                val priorityMapDrawCell = worldMapHelper.getPriorityMapDrawCell(map.posX, map.posY)
                if (map.id == priorityMapDrawCell?.mapId) {
                    Image(
                        BreedAssetManager.getAssets(characterUiState.dofusClassId).simpleIconPainter,
                        "",
                        modifier = Modifier.size(ExplorationUIUtil.CELL_SIZE.dp * 2).offset(
                            priorityMapDrawCell.topLeft.x.dp - ExplorationUIUtil.CELL_SIZE.dp / 2,
                            priorityMapDrawCell.topLeft.y.dp - ExplorationUIUtil.CELL_SIZE.dp / 2
                        )
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawSubArea(subAreaId: Double, wallColor: Color, wallWidth: Float) {
    val subAreaMaps = ExplorationUIUtil.worldMapHelper.value.mapDrawCellsBySubAreaId[subAreaId]
        ?: emptyList()
    val size = Size(ExplorationUIUtil.CELL_SIZE, ExplorationUIUtil.CELL_SIZE)
    val overColor = Color.White.copy(alpha = 0.2f)
    for (mapDrawCell in subAreaMaps) {
        drawRect(getColor(mapDrawCell.mapId), mapDrawCell.topLeft, size, blendMode = blendMode)
        drawRect(overColor, mapDrawCell.topLeft, size)
        drawLine(mapDrawCell.topWall, mapDrawCell.topLeft, mapDrawCell.topRight, wallColor, wallWidth)
        drawLine(mapDrawCell.leftWall, mapDrawCell.topLeft, mapDrawCell.bottomLeft, wallColor, wallWidth)
        drawLine(mapDrawCell.bottomWall, mapDrawCell.bottomLeft, mapDrawCell.bottomRight, wallColor, wallWidth)
        drawLine(mapDrawCell.rightWall, mapDrawCell.bottomRight, mapDrawCell.topRight, wallColor, wallWidth)
    }
}

private fun DrawScope.drawLine(
    isWallPresent: Boolean,
    from: Offset,
    to: Offset,
    wallColor: Color,
    wallWidth: Float
) {
    val (width, color) = if (isWallPresent) wallWidth to wallColor else 0.3f to Color.DarkGray
    drawLine(color, from, to, cap = cap, strokeWidth = width, blendMode = blendMode)
}

private fun DrawScope.drawCell(mapDrawCell: MapDrawCell, style: DrawStyle, color: Color) = drawRect(
    color = color,
    topLeft = mapDrawCell.topLeft,
    size = Size(ExplorationUIUtil.CELL_SIZE, ExplorationUIUtil.CELL_SIZE),
    blendMode = blendMode,
    style = style
)

@Composable
private fun PositionTooltip() {
    ExplorationUIUtil.mapUIState.value.hoveredMapDrawCell?.let { mapDrawCell ->
        val scale = ExplorationUIUtil.mapUIState.value.scale
        Row(
            Modifier.offset(mapDrawCell.topRight.x.dp, mapDrawCell.topRight.y.dp)
                .offset(
                    (-ExplorationUIUtil.centerX + mapDrawCell.x).dp * ExplorationUIUtil.CELL_SIZE * (scale - 1),
                    (-ExplorationUIUtil.centerY + mapDrawCell.y).dp * ExplorationUIUtil.CELL_SIZE * (scale - 1)
                ).padding(start = 10.dp)
                .background(AppColors.VERY_DARK_BG_COLOR)
                .padding(5.dp)
        ) {
            val characterNamesOnMap = CharactersUIUtil.getAllCharacterUIStates().map { it.value }
                .filter { it.currentMap?.id == mapDrawCell.mapId }.map { it.name }
            val charactersSuffix = if (characterNamesOnMap.isNotEmpty()) {
                " - ${characterNamesOnMap.joinToString(", ")}"
            } else ""
            CommonText("[${mapDrawCell.x},${mapDrawCell.y}]$charactersSuffix")
        }
    }
}

private fun calculateOffset(dragAmount: Offset, mapAvailableSize: Size) {
    val currentUIState = ExplorationUIUtil.mapUIState.value
    val scaledTotalWidth = ExplorationUIUtil.totalWidth * currentUIState.scale
    val scaledTotalHeight = ExplorationUIUtil.totalHeight * currentUIState.scale
    val maxOffset = Offset(
        abs(mapAvailableSize.width - scaledTotalWidth),
        abs(mapAvailableSize.height - scaledTotalHeight)
    )
    val minOffset = Offset(
        0f, 0f
    )
    val offset = currentUIState.offset + dragAmount
    ExplorationUIUtil.mapUIState.value = currentUIState.copy(
        offset = Offset(min(maxOffset.x, max(minOffset.x, offset.x)), min(maxOffset.y, max(minOffset.y, offset.y)))
    )
}
