package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.game.move.transporters.TravelUtil
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.util.network.GameInfo

open class TravelTask(private val destMaps: List<DofusMap>) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        if (destMaps.contains(gameInfo.currentMap)) {
            return true
        }
        val path = TravelUtil.getPath(gameInfo, destMaps) ?: error("Travel path not found")
        return MoveTask(path).run(logItem, gameInfo)
    }


    override fun onStarted(): String {
        val mapsStr = destMaps.map { it.getCoordinates() }
            .distinct()
            .joinToString(", ") { "(${it.x}; ${it.y})" }
        return "Traveling to $mapsStr ..."
    }

}