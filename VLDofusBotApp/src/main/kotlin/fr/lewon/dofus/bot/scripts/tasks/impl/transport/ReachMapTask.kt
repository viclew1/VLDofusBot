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

open class ReachMapTask(
    private val destMaps: List<DofusMap>,
    private val availableZaaps: List<DofusMap> = TravelUtil.getAllZaapMaps()
) :
    BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        if (destMaps.contains(gameInfo.currentMap)) {
            return true
        }
        LeaveHavenBagTask().run(logItem, gameInfo)

        val disabledZaapMapIds = TravelUtil.getAllZaapMaps().minus(availableZaaps.toSet()).map { it.id }
        val characterInfo = gameInfo.buildCharacterBasicInfo(disabledZaapMapIds)

        val fromVertex = getCurrentVertex(gameInfo)
            ?: error("No vertex found")
        val path = WorldGraphUtil.getPath(listOf(fromVertex), destMaps, characterInfo)
            ?: error("No path found to any of the destinations : ${getDestMapsStr()}")
        val destMap = path.lastOrNull()?.edge?.to?.mapId?.let { MapManager.getDofusMap(it) }
            ?: error("No transition in path")
        val subLogItem = gameInfo.logger.addSubLog("Moving to map : (${destMap.posX}; ${destMap.posY}) ...", logItem)
        return MoveTask(path).run(subLogItem, gameInfo).also {
            gameInfo.logger.closeLog(if (it) "OK" else "KO", subLogItem, true)
        }
    }

    private fun getDestMapsStr() = destMaps.map { it.coordinates }.distinct().joinToString(", ") {
        "(${it.x}; ${it.y})"
    }

    private fun getCurrentVertex(gameInfo: GameInfo): Vertex? {
        val playerCellId = gameInfo.entityPositionsOnMapByEntityId[gameInfo.playerId]
            ?: error("Couldn't find player cell id (player ID : ${gameInfo.playerId})")
        val cellData = gameInfo.mapData.completeCellDataByCellId[playerCellId]?.cellData
            ?: error("Couldn't find cell data (cell ID : $playerCellId)")
        return WorldGraphUtil.getVertex(gameInfo.currentMap.id, cellData.getLinkedZoneRP())
    }

    override fun onStarted(): String {
        return "Reaching a map ..."
    }

}