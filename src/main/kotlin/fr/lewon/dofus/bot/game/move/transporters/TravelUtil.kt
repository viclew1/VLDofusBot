package fr.lewon.dofus.bot.game.move.transporters

import fr.lewon.dofus.bot.core.manager.DofusMapManager
import fr.lewon.dofus.bot.core.manager.world.Transition
import fr.lewon.dofus.bot.core.manager.world.WorldGraphUtil
import fr.lewon.dofus.bot.core.model.maps.DofusCoordinates
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.util.network.GameInfo

object TravelUtil {

    fun getZaaps(altWorld: Boolean = false): List<Zaap> {
        return Zaap.values().filter {
            it.isAltWorld() == altWorld
        }
    }

    fun getTransporters(altWorld: Boolean = false): List<ITransporter> {
        return listOf<ITransporter>(
            *OtomaiTransporter.values(),
            *FrigostTransporter.values()
        ).filter { it.isAltWorld() == altWorld }
    }

    fun <T : ITravelElement> getClosestTravelElement(travelElements: List<T>, coordinates: DofusCoordinates): T? {
        return travelElements.minByOrNull { it.getCoordinates().distanceTo(coordinates) }
    }

    fun getPathByMapsId(gameInfo: GameInfo, destMapsIds: List<Double>): List<Transition>? {
        return getPath(gameInfo, destMapsIds.map { DofusMapManager.getDofusMap(it) })
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

}