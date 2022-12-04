package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.util.game.TravelUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

class TravelTask(private val destMaps: List<DofusMap>) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        if (destMaps.contains(gameInfo.currentMap)) {
            return true
        }
        val path = TravelUtil.getPath(gameInfo, destMaps)
            ?: error("Couldn't find a path to destination")
        val destMap = path.lastOrNull()?.edge?.to?.mapId?.let { MapManager.getDofusMap(it) }
            ?: error("No transition in path")
        gameInfo.logger.addSubLog("Travelling to destination : (${destMap.posX}; ${destMap.posY})", logItem)
        return MoveTask(path).run(logItem, gameInfo)
    }


    override fun onStarted(): String {
        return "Traveling to a map ..."
    }

}