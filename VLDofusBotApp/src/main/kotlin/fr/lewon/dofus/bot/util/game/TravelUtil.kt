package fr.lewon.dofus.bot.util.game

import fr.lewon.dofus.bot.core.d2o.managers.map.HintManager
import fr.lewon.dofus.bot.core.model.charac.DofusCharacterBasicInfo
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.world.Transition
import fr.lewon.dofus.bot.core.world.Vertex
import fr.lewon.dofus.bot.core.world.WorldGraphUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

object TravelUtil {

    fun getAllZaapMaps(): List<DofusMap> {
        return HintManager.getHints(HintManager.HintType.ZAAP)
            .map { it.map }
            .filter { !it.subArea.isConquestVillage }
    }

    fun getCurrentVertex(gameInfo: GameInfo): Vertex {
        val playerCellId = gameInfo.entityPositionsOnMapByEntityId[gameInfo.playerId]
            ?: error("Can't find player cell id")
        val cellData = gameInfo.mapData.completeCellDataByCellId[playerCellId]?.cellData
            ?: error("Can't find player cell data")
        return WorldGraphUtil.getVertex(gameInfo.currentMap.id, cellData.getLinkedZoneRP())
            ?: error("No vertex found")
    }

    fun getPath(gameInfo: GameInfo, destMap: DofusMap, characterInfo: DofusCharacterBasicInfo): List<Transition>? {
        return getPath(gameInfo, listOf(destMap), characterInfo)
    }

    fun getReversePath(
        gameInfo: GameInfo,
        destMap: DofusMap,
        characterInfo: DofusCharacterBasicInfo
    ): List<Transition>? = WorldGraphUtil.getPath(destMap.id, 1, listOf(gameInfo.currentMap.id), characterInfo)

    fun getPath(
        gameInfo: GameInfo, destMaps: List<DofusMap>,
        characterInfo: DofusCharacterBasicInfo
    ): List<Transition>? {
        val playerCellId = gameInfo.entityPositionsOnMapByEntityId[gameInfo.playerId]
            ?: error("Couldn't find player cell id (player ID : ${gameInfo.playerId})")
        val cellData = gameInfo.mapData.completeCellDataByCellId[playerCellId]?.cellData
            ?: error("Couldn't find cell data (cell ID : $playerCellId)")
        return WorldGraphUtil.getPath(
            gameInfo.currentMap.id,
            cellData.getLinkedZoneRP(),
            destMaps.map { it.id },
            characterInfo
        )
    }

}