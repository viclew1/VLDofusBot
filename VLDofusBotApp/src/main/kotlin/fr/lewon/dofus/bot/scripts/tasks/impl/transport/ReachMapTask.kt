package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.world.WorldGraphUtil
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.MoveTask
import fr.lewon.dofus.bot.util.game.TravelUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

open class ReachMapTask(
    private val destMaps: List<DofusMap>,
    private val availableZaaps: List<DofusMap> = TravelUtil.getAllZaapMaps(),
    private val harvestEnabled: Boolean = true,
) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        if (destMaps.contains(gameInfo.currentMap)) {
            return true
        }
        LeaveHavenBagTask().run(logItem, gameInfo)

        val disabledZaapMapIds = TravelUtil.getAllZaapMaps().minus(availableZaaps.toSet()).map { it.id }
        val characterInfo = gameInfo.buildCharacterBasicInfo(disabledZaapMapIds)

        val fromVertex = TravelUtil.getCurrentVertex(gameInfo)
        val path = WorldGraphUtil.getPath(listOf(fromVertex), destMaps.map { it.id }, characterInfo)
            ?: error("No path found to any of the destinations : ${getDestMapsStr()}")
        val destMap = path.lastOrNull()?.edge?.to?.mapId?.let { MapManager.getDofusMap(it) }
            ?: error("No transition in path")
        val subLogItem = gameInfo.logger.addSubLog("Moving to map : ${destMap.coordinates} ...", logItem)
        return MoveTask(transitions = path, harvestEnabled = harvestEnabled).run(subLogItem, gameInfo).also {
            gameInfo.logger.closeLog(if (it) "OK" else "KO", subLogItem, it)
        }
    }

    private fun getDestMapsStr() = destMaps.map { it.coordinates.toString() }.distinct().joinToString(", ")

    override fun onStarted(): String {
        return "Reaching a map ..."
    }

}