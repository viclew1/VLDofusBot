package fr.lewon.dofus.bot.gui.main.exploration.map

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.input.pointer.isCtrlPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.gui.custom.CommonText
import fr.lewon.dofus.bot.gui.custom.handPointerIcon
import fr.lewon.dofus.bot.gui.custom.onMouseMove
import fr.lewon.dofus.bot.gui.main.characters.CharacterActivityState
import fr.lewon.dofus.bot.gui.main.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui.main.exploration.ExplorationUIUtil
import fr.lewon.dofus.bot.gui.main.exploration.map.helper.MapDrawCell
import fr.lewon.dofus.bot.gui.main.exploration.map.helper.WorldMapHelperOverlay
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.model.characters.paths.SubPath
import fr.lewon.dofus.bot.scripts.impl.ExploreMapsScriptBuilder
import fr.lewon.dofus.bot.util.filemanagers.impl.BreedAssetManager
import fr.lewon.dofus.bot.util.filemanagers.impl.ExplorationRecordManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val cap = StrokeCap.Square
private val blendMode = BlendMode.Src
private val colorByMapId = mutableStateOf(HashMap<Double, Color>())
private const val characterIconSize = 3f
private const val characterIconDelta = (characterIconSize - 1f) / 2f
private val cellSize = Size(ExplorationUIUtil.CellSize, ExplorationUIUtil.CellSize)
private val overColor = Color.White.copy(alpha = 0.2f)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExplorationMapContent() {
    val mapUiStateValue = ExplorationUIUtil.mapUIState.value
    val modifier = if (mapUiStateValue.hoveredMapDrawCell != null) {
        Modifier.handPointerIcon()
    } else Modifier
    val coroutineScope = rememberCoroutineScope()
    val dragging = remember { mutableStateOf(false) }
    Box(
        modifier.fillMaxSize()
            .horizontalScroll(mapUiStateValue.horizontalScrollState, false)
            .verticalScroll(mapUiStateValue.verticalScrollState, false)
            .clip(RectangleShape)
            .onDrag(
                onDragStart = { dragging.value = true },
                onDragEnd = { dragging.value = false },
                onDragCancel = { dragging.value = false },
                onDrag = { dragAmount ->
                    coroutineScope.launch {
                        ExplorationUIUtil.mapUIState.value.horizontalScrollState.scrollBy(-dragAmount.x)
                        ExplorationUIUtil.mapUIState.value.verticalScrollState.scrollBy(-dragAmount.y)
                    }
                }
            ).onMouseMove { location, _ ->
                if (!dragging.value) {
                    ExplorationUIUtil.mapUIState.value = ExplorationUIUtil.mapUIState.value.copy(
                        hoveredMapDrawCell = getMapDrawCell(location)
                    )
                }
            }.onClick {
                ExplorationUIUtil.mapUIState.value = ExplorationUIUtil.mapUIState.value.copy(
                    selectedSubAreaIds = emptyList()
                )
            }
    ) {
        val refreshColors = {
            val newColorByMap = HashMap<Double, Color>()
            val now = System.currentTimeMillis()
            val maxExplorationAge = 2 * 3600 * 1000
            val oldestExplorationTime = now - maxExplorationAge
            for ((mapId, time) in ExplorationRecordManager.getExploredTimeByMapId()) {
                val lastExploreTime = maxOf(oldestExplorationTime, time)
                val red = 255 * (now - lastExploreTime) / maxExplorationAge
                val blue = 255 - red
                newColorByMap[mapId] = Color(red.toInt(), 0, blue.toInt())
            }
            colorByMapId.value = newColorByMap
        }
        if (ExplorationUIUtil.mapUpdated.value) {
            refreshColors()
            ExplorationUIUtil.mapUpdated.value = false
        }
        LaunchedEffect(Unit) {
            while (true) {
                delay(10000)
                refreshColors()
            }
        }
        Box(Modifier.requiredSize(ExplorationUIUtil.totalWidth.dp, ExplorationUIUtil.totalHeight.dp)) {
            Box {
                OverlayContent(WorldMapHelperOverlay)
                when (ExplorationUIUtil.explorationTypeUiState.value) {
                    ExploreMapsScriptBuilder.ExplorationType.SubArea -> {
                        CellsContent()
                        OverlayContent(ExplorationUIUtil.worldMapHelper.value.mapOverlayPainter)
                    }
                    ExploreMapsScriptBuilder.ExplorationType.Path -> {

                    }
                }
                if (ExplorationUIUtil.explorationTypeUiState.value == ExploreMapsScriptBuilder.ExplorationType.SubArea) {
                    CellsContent()
                    OverlayContent(ExplorationUIUtil.worldMapHelper.value.mapOverlayPainter)
                }
            }
            Box {
                when (ExplorationUIUtil.explorationTypeUiState.value) {
                    ExploreMapsScriptBuilder.ExplorationType.SubArea -> SubAreaOverlay()
                    ExploreMapsScriptBuilder.ExplorationType.Path -> PathOverlay()
                }
                CharactersContent()
            }
            Box {
                if (ExplorationUIUtil.explorationTypeUiState.value == ExploreMapsScriptBuilder.ExplorationType.Path) {
                    HoveredSubPathTooltip()
                }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CellsContent() {
    Canvas(Modifier.fillMaxSize().pointerInput(Unit) {
        detectTapGestures(keyboardModifiers = { this.isCtrlPressed }) {
            val mapUiStateValue = ExplorationUIUtil.mapUIState.value
            val clickedSubAreaId = getMapDrawCell(it)?.subAreaId
            if (clickedSubAreaId != null) {
                val clickedSubAreaIndex = mapUiStateValue.selectedSubAreaIds.indexOf(clickedSubAreaId)
                val newUiStateValue = if (clickedSubAreaIndex >= 0) {
                    mapUiStateValue.copy(
                        selectedSubAreaIds = mapUiStateValue.selectedSubAreaIds.minus(clickedSubAreaId),
                        selectedSubAreaIndex = if (mapUiStateValue.selectedSubAreaIndex >= clickedSubAreaIndex) {
                            mapUiStateValue.selectedSubAreaIndex - 1
                        } else mapUiStateValue.selectedSubAreaIndex
                    )
                } else if (mapUiStateValue.selectedSubAreaIds.size < ExplorationUIUtil.MinAreasToExplore) {
                    mapUiStateValue.copy(
                        selectedSubAreaIds = mapUiStateValue.selectedSubAreaIds.plus(clickedSubAreaId),
                        selectedSubAreaIndex = mapUiStateValue.selectedSubAreaIds.size
                    )
                } else mapUiStateValue
                ExplorationUIUtil.mapUIState.value = newUiStateValue
            }
        }
    }.pointerInput(Unit) {
        detectTapGestures(keyboardModifiers = { !this.isCtrlPressed }) {
            val mapUiStateValue = ExplorationUIUtil.mapUIState.value
            val clickedSubAreaId = getMapDrawCell(it)?.subAreaId
            if (clickedSubAreaId != null) {
                ExplorationUIUtil.mapUIState.value = mapUiStateValue.copy(
                    selectedSubAreaIds = listOf(clickedSubAreaId),
                    selectedSubAreaIndex = 0
                )
            } else {
                ExplorationUIUtil.mapUIState.value = mapUiStateValue.copy(
                    selectedSubAreaIds = emptyList()
                )
            }
        }
    }) {
        for (mapDrawCell in ExplorationUIUtil.worldMapHelper.value.priorityMapDrawCells) {
            drawCell(mapDrawCell.topLeft, Fill, getColor(mapDrawCell.mapId))
        }
    }
}

private fun getMapDrawCell(position: Offset): MapDrawCell? =
    ExplorationUIUtil.worldMapHelper.value.getPriorityMapDrawCell(
        ExplorationUIUtil.minPosX + (position.x / ExplorationUIUtil.CellSize).toInt(),
        ExplorationUIUtil.minPosY + (position.y / ExplorationUIUtil.CellSize).toInt(),
    )

private fun getColor(mapId: Double): Color = colorByMapId.value[mapId] ?: Color.Red

@Composable
private fun SubAreaOverlay() {
    Canvas(Modifier.fillMaxSize()) {
        val mapUiStateValue = ExplorationUIUtil.mapUIState.value
        val selectedSubAreaIds = mapUiStateValue.selectedSubAreaIds
        val hoveredMapDrawCell = mapUiStateValue.hoveredMapDrawCell
        mapUiStateValue.areaExploredByCharacter.forEach { (character, subArea) ->
            val wallWidth = if (subArea.id in selectedSubAreaIds || hoveredMapDrawCell?.subAreaId == subArea.id) {
                4f
            } else 2f
            drawSubArea(subArea.id, Color.Yellow, wallWidth)
        }
        selectedSubAreaIds.sortedByDescending {
            val maps = ExplorationUIUtil.worldMapHelper.value.mapDrawCellsBySubAreaId[it] ?: emptyList()
            maps.size
        }.forEach {
            drawSubArea(it, AppColors.primaryDarkColor, 1.8f)
        }
        hoveredMapDrawCell?.let {
            drawSubArea(hoveredMapDrawCell.subAreaId, AppColors.primaryColor, 1.5f)
            drawCell(hoveredMapDrawCell.topLeft, Stroke(1f), AppColors.primaryDarkColor)
        }
    }
}

@Composable
private fun PathOverlay() {
    Canvas(Modifier.fillMaxSize()) {
        val selectedPath = ExplorationUIUtil.selectedPath.value
        if (selectedPath != null) {
            val mapsBySubPath = selectedPath.subPaths.associateWith {
                it.mapIds.distinct().map(MapManager::getDofusMap)
            }
            val hoveredSubPath = getHoveredSubPath()
            hoveredSubPath?.let {
                val maps = mapsBySubPath[hoveredSubPath] ?: emptyList()
                drawSubPath(maps, Stroke(2f)) { AppColors.primaryLightColor }
            }
            for (subPath in selectedPath.subPaths) {
                val maps = mapsBySubPath[subPath] ?: emptyList()
                drawSubPath(maps, Fill) { if (subPath.enabled) getColor(it.id) else Color.DarkGray }
            }
        }
    }
}

private fun getHoveredSubPath(): SubPath? {
    val selectedPath = ExplorationUIUtil.selectedPath.value
        ?: return null
    val mapUiStateValue = ExplorationUIUtil.mapUIState.value
    val hoveredMapDrawCell = mapUiStateValue.hoveredMapDrawCell
    return hoveredMapDrawCell?.let {
        selectedPath.subPaths.firstOrNull { hoveredMapDrawCell.mapId in it.mapIds }
    }
}

@Composable
private fun CharactersContent() {
    Box(Modifier.fillMaxSize()) {
        val worldMapHelper = ExplorationUIUtil.worldMapHelper.value
        val connectedCharactersUIStates = CharactersUIUtil.getAllCharacterUIStates()
            .filter { it.activityState != CharacterActivityState.DISCONNECTED && it.activityState != CharacterActivityState.TO_INITIALIZE }
        for (characterUiState in connectedCharactersUIStates) {
            val map = characterUiState.currentMap
            if (map != null) {
                val priorityMapDrawCell = worldMapHelper.getPriorityMapDrawCell(map.posX, map.posY)
                if (map.id == priorityMapDrawCell?.mapId) {
                    Image(
                        BreedAssetManager.getAssets(characterUiState.dofusClassId).simpleIconPainter,
                        "",
                        modifier = Modifier.size((ExplorationUIUtil.CellSize * characterIconSize).dp).offset(
                            priorityMapDrawCell.topLeft.x.dp - (ExplorationUIUtil.CellSize * characterIconDelta).dp,
                            priorityMapDrawCell.topLeft.y.dp - (ExplorationUIUtil.CellSize * characterIconDelta).dp
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
    for (mapDrawCell in subAreaMaps) {
        drawRect(getColor(mapDrawCell.mapId), mapDrawCell.topLeft, cellSize, blendMode = blendMode)
        drawRect(overColor, mapDrawCell.topLeft, cellSize)
        drawLine(mapDrawCell.topWall, mapDrawCell.topLeft, mapDrawCell.topRight, wallColor, wallWidth)
        drawLine(mapDrawCell.leftWall, mapDrawCell.topLeft, mapDrawCell.bottomLeft, wallColor, wallWidth)
        drawLine(mapDrawCell.bottomWall, mapDrawCell.bottomLeft, mapDrawCell.bottomRight, wallColor, wallWidth)
        drawLine(mapDrawCell.rightWall, mapDrawCell.bottomRight, mapDrawCell.topRight, wallColor, wallWidth)
    }
}

private fun DrawScope.drawSubPath(subPathMaps: List<DofusMap>, style: DrawStyle, getMapColor: (DofusMap) -> Color) {
    for (map in subPathMaps) {
        val x = map.posX - ExplorationUIUtil.minPosX
        val y = map.posY - ExplorationUIUtil.minPosY
        val topLeft = Offset(x * ExplorationUIUtil.CellSize, y * ExplorationUIUtil.CellSize)
        drawCell(topLeft, style, getMapColor(map))
    }
}

private fun getTopLeftOffset(map: DofusMap): Offset = getTopLeftOffset(map.posX, map.posY)

private fun getTopLeftOffset(posX: Int, posY: Int): Offset {
    val x = posX - ExplorationUIUtil.minPosX
    val y = posY - ExplorationUIUtil.minPosY
    return Offset(x * ExplorationUIUtil.CellSize, y * ExplorationUIUtil.CellSize)
}

private fun DrawScope.drawLine(
    isWallPresent: Boolean,
    from: Offset,
    to: Offset,
    wallColor: Color,
    wallWidth: Float,
) {
    val (width, color) = if (isWallPresent) wallWidth to wallColor else 0.3f to Color.DarkGray
    drawLine(color, from, to, cap = cap, strokeWidth = width, blendMode = blendMode)
}

private fun DrawScope.drawCell(topLeft: Offset, style: DrawStyle, color: Color) = drawRect(
    color = color,
    topLeft = topLeft,
    size = Size(ExplorationUIUtil.CellSize, ExplorationUIUtil.CellSize),
    blendMode = blendMode,
    style = style
)

@Composable
private fun PositionTooltip() {
    ExplorationUIUtil.mapUIState.value.hoveredMapDrawCell?.let { mapDrawCell ->
        Row(
            Modifier.offset(mapDrawCell.topRight.x.dp, mapDrawCell.topRight.y.dp)
                .padding(start = 10.dp)
                .background(AppColors.VERY_DARK_BG_COLOR)
                .padding(5.dp)
        ) {
            val characterNamesOnMap = CharactersUIUtil.getAllCharacterUIStates()
                .filter { it.currentMap?.id == mapDrawCell.mapId }.map { it.name }
            val charactersSuffix = if (characterNamesOnMap.isNotEmpty()) {
                " - ${characterNamesOnMap.joinToString(", ")}"
            } else ""
            CommonText("[${mapDrawCell.x},${mapDrawCell.y}]$charactersSuffix")
        }
    }
}

@Composable
private fun HoveredSubPathTooltip() {
    getHoveredSubPath()?.let { hoveredSubPath ->
        val subPathMaps = hoveredSubPath.mapIds.map(MapManager::getDofusMap)
        val minY = subPathMaps.minOfOrNull { it.posY }
        val topLeftMap = subPathMaps.filter { it.posY == minY }.minByOrNull { it.posX }
        if (topLeftMap != null) {
            val offset = getTopLeftOffset(topLeftMap.posX - 1, topLeftMap.posY - 1)
            val tooltipSize = remember(topLeftMap) { mutableStateOf(IntSize(0, 0)) }
            Row(
                Modifier.alpha(if (tooltipSize.value.width == 0) 0f else 1f)
                    .offset((offset.x - tooltipSize.value.width).dp, (offset.y - tooltipSize.value.height).dp)
                    .padding(start = 10.dp)
                    .background(AppColors.VERY_DARK_BG_COLOR)
                    .onGloballyPositioned { tooltipSize.value = it.size }
            ) {
                CommonText(
                    "${hoveredSubPath.displayName} (${subPathMaps.size} maps)",
                    modifier = Modifier.padding(5.dp)
                )
            }
        }
    }
}