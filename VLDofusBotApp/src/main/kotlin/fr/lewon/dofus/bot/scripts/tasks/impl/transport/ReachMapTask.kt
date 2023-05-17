package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.world.Vertex
import fr.lewon.dofus.bot.core.world.WorldGraphUtil
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.MoveTask
import fr.lewon.dofus.bot.util.game.TravelUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

open class ReachMapTask(private val destMaps: List<DofusMap>) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        if (destMaps.contains(gameInfo.currentMap)) {
            return true
        }

        val allZaapMaps = TravelUtil.getAllZaapMaps()
        allZaapMaps.intersect(destMaps.toSet()).takeIf { it.isNotEmpty() }?.let {
            return ZaapTowardTask(it.first()).run(logItem, gameInfo)
        }
        val allZaapMapIds = allZaapMaps.map { it.id }
        val currentVertex = getCurrentVertex(gameInfo)
        val fromVertices = allZaapMapIds.map { WorldGraphUtil.getVertex(it, 1) }.plus(currentVertex).filterNotNull()
        val path = WorldGraphUtil.getPath(fromVertices, destMaps, gameInfo.buildCharacterBasicInfo())
        if (path == null) {
            val mapsStr = destMaps.map { it.coordinates }.distinct().joinToString(", ") { "(${it.x}; ${it.y})" }
            error("No path found to any of the destinations : $mapsStr")
        }
        val destMap = path.lastOrNull()?.edge?.to?.mapId?.let { MapManager.getDofusMap(it) }
            ?: error("No transition in path")
        val subLogItem = gameInfo.logger.addSubLog("Moving to map : (${destMap.posX}; ${destMap.posY}) ...", logItem)
        val fromMap = path.first().edge.from.mapId
        val zaapOk = if (allZaapMapIds.contains(fromMap)) {
            ZaapTowardTask(MapManager.getDofusMap(fromMap)).run(subLogItem, gameInfo)
        } else true
        return zaapOk && MoveTask(path).run(subLogItem, gameInfo).also {
            gameInfo.logger.closeLog("OK", subLogItem, true)
        }
    }

    private fun getCurrentVertex(gameInfo: GameInfo): Vertex? {
        val playerCellId = gameInfo.entityPositionsOnMapByEntityId[gameInfo.playerId]
            ?: error("Couldn't find player cell id (player ID : ${gameInfo.playerId})")
        val cellData = gameInfo.completeCellDataByCellId[playerCellId]?.cellData
            ?: error("Couldn't find cell data (cell ID : $playerCellId)")
        return WorldGraphUtil.getVertex(gameInfo.currentMap.id, cellData.getLinkedZoneRP())
    }

    override fun onStarted(): String {
        return "Reaching a map ..."
    }

}