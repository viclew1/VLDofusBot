package fr.lewon.dofus.bot.gui2.main.exploration

import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.d2o.managers.map.SubAreaManager
import fr.lewon.dofus.bot.core.model.maps.DofusMap

object ExplorationUIUtil {

    val uiState = mutableStateOf(ExplorationUIState())

    private val subAreas by lazy {
        SubAreaManager.getAllSubAreas().filter { it.displayOnWorldMap }
    }

    val mapDrawCellBySubArea by lazy {
        subAreas.associateWith { subArea ->
            val maps = subArea.mapIds.map(MapManager::getDofusMap).filter { it.worldMap?.id == 1 }
            maps.map { buildMapDrawCell(it, maps) }
        }
    }

    private fun buildMapDrawCell(map: DofusMap, maps: List<DofusMap>): MapDrawCell {
        return MapDrawCell(
            map,
            leftWall = maps.none { it.posX == map.posX - 1 && it.posY == map.posY },
            rightWall = maps.none { it.posX == map.posX + 1 && it.posY == map.posY },
            bottomWall = maps.none { it.posX == map.posX && it.posY == map.posY + 1 },
            topWall = maps.none { it.posX == map.posX && it.posY == map.posY - 1 }
        )
    }

    fun exploreMap(mapId: Double) {
        uiState.value = uiState.value.copy(
            exploredTimeByMap = uiState.value.exploredTimeByMap.plus(mapId to System.currentTimeMillis())
        )
    }

}