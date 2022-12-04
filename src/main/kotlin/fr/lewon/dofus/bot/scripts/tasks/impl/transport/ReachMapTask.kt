package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusMap
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

        val fromZaapPath = WorldGraphUtil.getPath(allZaapMaps, destMaps, gameInfo.buildCharacterBasicInfo())
        val walkingPath = TravelUtil.getPath(gameInfo, destMaps)

        val pathToDest = when {
            fromZaapPath != null && (walkingPath == null || fromZaapPath.size < walkingPath.size - 2) -> fromZaapPath
            walkingPath != null -> walkingPath
            else -> {
                val mapsStr = destMaps.map { it.coordinates }.distinct()
                    .joinToString(", ") { "(${it.x}; ${it.y})" }
                error("No path found to any of the destinations : $mapsStr")
            }
        }

        val destMap = pathToDest.lastOrNull()?.edge?.to?.mapId?.let { MapManager.getDofusMap(it) }
            ?: error("No transition in path")

        val subLogItem =
            gameInfo.logger.addSubLog("Moving to destination : (${destMap.posX}; ${destMap.posY}) ...", logItem)

        val zaapOk = if (pathToDest != walkingPath) {
            val zaapMap = MapManager.getDofusMap(pathToDest.first().edge.from.mapId)
            ZaapTowardTask(zaapMap).run(subLogItem, gameInfo)
        } else true
        return zaapOk && MoveTask(pathToDest).run(subLogItem, gameInfo).also {
            gameInfo.logger.closeLog("OK", subLogItem, true)
        }
    }

    override fun onStarted(): String {
        return "Reaching a map ..."
    }

}