package fr.lewon.dofus.bot.game.move.transporters

import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.d2o.managers.map.WaypointManager
import fr.lewon.dofus.bot.core.model.maps.DofusCoordinates
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.world.Transition
import fr.lewon.dofus.bot.core.world.WorldGraphUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

object TravelUtil {

    fun getTransporters(): List<ITransporter> {
        return listOf<ITransporter>(
            *OtomaiTransporter.values(),
            *FrigostTransporter.values()
        )
    }

    fun getClosestTransporter(
        gameInfo: GameInfo,
        transporters: List<ITransporter>,
        maps: List<DofusMap>
    ): Pair<ITransporter, Int>? {
        return getClosest(gameInfo, transporters, maps) { it.getMap() }
    }

    fun getClosestZaap(gameInfo: GameInfo, maps: List<DofusMap>): Pair<DofusMap, Int>? {
        val zaaps = WaypointManager.getAllZaapMaps()
            .sortedBy { minDistance(it, maps) }
        val zaapsSubList = zaaps.takeIf { it.size > 3 }?.subList(0, 3) ?: zaaps
        return getClosest(gameInfo, zaapsSubList, maps) { it }
    }

    private fun minDistance(fromMap: DofusMap, toMaps: List<DofusMap>): Int {
        return toMaps.map { getDistance(fromMap, it) }.minOrNull() ?: Int.MAX_VALUE
    }

    private fun getDistance(fromMap: DofusMap, toMap: DofusMap): Int {
        return toMap.getCoordinates().distanceTo(fromMap.getCoordinates())
    }

    private fun <T> getClosest(
        gameInfo: GameInfo,
        items: List<T>,
        toMaps: List<DofusMap>,
        mapGetter: (T) -> DofusMap
    ): Pair<T, Int>? {
        return items.map { it to getPath(mapGetter(it), 1, toMaps, gameInfo) }
            .filter { it.second != null }
            .map { it.first to (it.second?.size ?: Int.MAX_VALUE) }
            .minByOrNull { it.second }
    }

    fun getPath(gameInfo: GameInfo, destMap: DofusMap): List<Transition>? {
        return getPath(gameInfo, listOf(destMap))
    }

    fun getReversePath(gameInfo: GameInfo, destMap: DofusMap): List<Transition>? {
        return WorldGraphUtil.getPath(destMap, 1, listOf(gameInfo.currentMap), gameInfo.buildCharacterBasicInfo())
    }

    fun getPath(gameInfo: GameInfo, destCoordinates: DofusCoordinates): List<Transition>? {
        return getPath(gameInfo, MapManager.getDofusMaps(destCoordinates.x, destCoordinates.y))
    }

    fun getPath(gameInfo: GameInfo, destMaps: List<DofusMap>): List<Transition>? {
        val playerCellId = gameInfo.entityPositionsOnMapByEntityId[gameInfo.playerId]
            ?: error("Couldn't find played cell id (player ID : ${gameInfo.playerId})")
        val cellData = gameInfo.completeCellDataByCellId[playerCellId]?.cellData
            ?: error("Couldn't find cell data (cell ID : $playerCellId)")
        return WorldGraphUtil.getPath(
            gameInfo.currentMap,
            cellData.getLinkedZoneRP(),
            destMaps,
            gameInfo.buildCharacterBasicInfo()
        )
    }

    fun getPath(fromMap: DofusMap, fromZone: Int, destMaps: List<DofusMap>, gameInfo: GameInfo): List<Transition>? {
        return WorldGraphUtil.getPath(fromMap, fromZone, destMaps, gameInfo.buildCharacterBasicInfo())
    }

}