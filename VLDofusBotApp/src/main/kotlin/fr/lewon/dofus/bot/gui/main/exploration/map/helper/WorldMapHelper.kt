package fr.lewon.dofus.bot.gui.main.exploration.map.helper

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toPainter
import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.d2o.managers.map.SubAreaManager
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.gui.main.exploration.ExplorationUIUtil
import fr.lewon.dofus.bot.gui.util.UiResource
import java.awt.Color
import java.awt.Graphics2D

abstract class WorldMapHelper(val name: String, val icon: UiResource) {

    val displayedSubAreas: List<DofusSubArea>
    val mapDrawCellsBySubAreaId: Map<Double, List<MapDrawCell>>
    private val mapDrawCellByMapId: Map<Double, MapDrawCell>
    val priorityMapDrawCells: List<MapDrawCell>
    val mapOverlayPainter: Painter

    init {
        displayedSubAreas = SubAreaManager.getAllSubAreas().filter(this::isDisplayedOnMap)
        mapDrawCellsBySubAreaId = displayedSubAreas.associate { subArea ->
            val subAreaMaps = subArea.mapIds.map(MapManager::getDofusMap).filter(this::isMapValid)
            subArea.id to subAreaMaps.map { buildMapDrawCell(it, subAreaMaps) }
        }
        mapDrawCellByMapId = mapDrawCellsBySubAreaId.flatMap { it.value }.associateBy { it.mapId }
        priorityMapDrawCells = mapDrawCellByMapId.values.filter { getPriorityMapDrawCell(it.x, it.y) == it }
        mapOverlayPainter = buildOverlayImage { graphics, x, y ->
            getPriorityMapDrawCell(x, y)?.drawCellLines(graphics)
        }.toPainter()
    }

    private fun MapDrawCell.drawCellLines(graphics: Graphics2D) {
        graphics.drawCellLine(topLeft, topRight, topWall)
        graphics.drawCellLine(topLeft, bottomLeft, leftWall)
        graphics.drawCellLine(bottomLeft, bottomRight, bottomWall)
        graphics.drawCellLine(bottomRight, topRight, rightWall)
    }

    private fun Graphics2D.drawCellLine(from: Offset, to: Offset, isWallPresent: Boolean) {
        val (stroke, color) = if (isWallPresent) {
            1f to Color.BLACK
        } else {
            0.25f to Color.DARK_GRAY
        }
        this.drawCellLine(from, to, stroke, color)
    }

    private fun buildMapDrawCell(map: DofusMap, subAreaMaps: Collection<DofusMap>): MapDrawCell {
        val x = map.posX - ExplorationUIUtil.minPosX
        val y = map.posY - ExplorationUIUtil.minPosY
        return MapDrawCell(
            mapId = map.id,
            subAreaId = map.subArea.id,
            x = map.posX,
            y = map.posY,
            topLeft = Offset(x * ExplorationUIUtil.CELL_SIZE, y * ExplorationUIUtil.CELL_SIZE),
            topRight = Offset((x + 1) * ExplorationUIUtil.CELL_SIZE, y * ExplorationUIUtil.CELL_SIZE),
            bottomLeft = Offset(x * ExplorationUIUtil.CELL_SIZE, (y + 1) * ExplorationUIUtil.CELL_SIZE),
            bottomRight = Offset((x + 1) * ExplorationUIUtil.CELL_SIZE, (y + 1) * ExplorationUIUtil.CELL_SIZE),
            leftWall = subAreaMaps.none { it.posX == map.posX - 1 && it.posY == map.posY },
            rightWall = subAreaMaps.none { it.posX == map.posX + 1 && it.posY == map.posY },
            bottomWall = subAreaMaps.none { it.posX == map.posX && it.posY == map.posY + 1 },
            topWall = subAreaMaps.none { it.posX == map.posX && it.posY == map.posY - 1 }
        )
    }

    fun getPriorityMapDrawCell(x: Int, y: Int): MapDrawCell? {
        val maps = MapManager.getDofusMaps(x, y).filter(this::isMapValid)
        val mapId = maps.firstOrNull {
            it.subArea.id == ExplorationUIUtil.mapUIState.value.selectedSubAreaIds.firstOrNull()
        }?.id ?: getPriorityMap(maps.filter { displayedSubAreas.contains(it.subArea) })?.id
        return mapDrawCellByMapId[mapId]
    }

    abstract fun isMapValid(map: DofusMap): Boolean

    abstract fun isDisplayedOnMap(subArea: DofusSubArea): Boolean

    abstract fun getPriorityMap(maps: List<DofusMap>): DofusMap?

}