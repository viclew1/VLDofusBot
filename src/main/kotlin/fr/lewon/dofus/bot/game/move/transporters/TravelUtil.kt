package fr.lewon.dofus.bot.game.move.transporters

import fr.lewon.dofus.bot.core.VLDofusBotCoreUtil
import fr.lewon.dofus.bot.core.manager.DofusMapManager
import fr.lewon.dofus.bot.core.manager.WaypointsManager
import fr.lewon.dofus.bot.core.manager.d2o.D2OUtil
import fr.lewon.dofus.bot.core.manager.world.Transition
import fr.lewon.dofus.bot.core.manager.world.WorldGraphUtil
import fr.lewon.dofus.bot.core.model.maps.DofusCoordinates
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.util.network.GameInfo

object TravelUtil {

    fun getTransporters(): List<ITransporter> {
        return listOf<ITransporter>(
            *OtomaiTransporter.values(),
            *FrigostTransporter.values()
        )
    }

    fun getClosestTransporter(transporters: List<ITransporter>, maps: List<DofusMap>): Pair<ITransporter, Int>? {
        return getClosest(transporters, maps) { it.getMap() }
    }

    fun getClosestZaap(maps: List<DofusMap>): Pair<DofusMap, Int>? {
        val worldMaps = maps.map { it.worldMap }
        val zaaps = WaypointsManager.getAllZaapMaps()
            .filter { worldMaps.contains(it.worldMap) }
            .sortedBy { minDistance(it, maps) }
        val zaapsSubList = zaaps.takeIf { it.size > 3 }?.subList(0, 3) ?: zaaps
        return getClosest(zaapsSubList, maps) { it }
    }

    private fun minDistance(fromMap: DofusMap, toMaps: List<DofusMap>): Int {
        return toMaps.map { getDistance(fromMap, it) }.minOrNull() ?: Int.MAX_VALUE
    }

    private fun getDistance(fromMap: DofusMap, toMap: DofusMap): Int {
        if (fromMap.worldMap != toMap.worldMap) {
            return Int.MAX_VALUE
        }
        return toMap.getCoordinates().distanceTo(fromMap.getCoordinates())
    }

    private fun <T> getClosest(items: List<T>, toMaps: List<DofusMap>, mapGetter: (T) -> DofusMap): Pair<T, Int>? {
        return items.map { it to getPath(mapGetter(it), 1, toMaps) }
            .filter { it.second != null }
            .map { it.first to (it.second?.size ?: Int.MAX_VALUE) }
            .minByOrNull { it.second }
    }

    fun getPath(gameInfo: GameInfo, destMap: DofusMap): List<Transition>? {
        return getPath(gameInfo, listOf(destMap))
    }

    fun getPath(gameInfo: GameInfo, destCoordinates: DofusCoordinates): List<Transition>? {
        return getPath(gameInfo, DofusMapManager.getDofusMaps(destCoordinates.x, destCoordinates.y))
    }

    fun getPath(gameInfo: GameInfo, destMaps: List<DofusMap>): List<Transition>? {
        val playerCellId = gameInfo.entityPositionsOnMapByEntityId[gameInfo.playerId]
            ?: return null
        val cellData = gameInfo.completeCellDataByCellId[playerCellId]?.cellData
            ?: return null
        return WorldGraphUtil.getPath(gameInfo.currentMap, cellData.getLinkedZoneRP(), destMaps)
    }

    fun getPath(fromMap: DofusMap, fromZone: Int, destMaps: List<DofusMap>): List<Transition>? {
        return WorldGraphUtil.getPath(fromMap, fromZone, destMaps)
    }

}

fun main() {
    VLDofusBotCoreUtil.initAll()
    val waypoints = D2OUtil.getObjects("Waypoints")
    waypoints.forEach {
        println(
            "${it["activated"]} - ${
                DofusMapManager.getDofusMap(it["mapId"].toString().toDouble())
            }"
        )
    }
}