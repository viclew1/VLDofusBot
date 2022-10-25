package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.TravelTask
import fr.lewon.dofus.bot.util.game.TravelUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

open class ReachMapTask(private val dofusMaps: List<DofusMap>) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        if (dofusMaps.contains(gameInfo.currentMap)) {
            return true
        }

        val zaapWithDist = TravelUtil.getClosestZaap(gameInfo, dofusMaps)
        val path = TravelUtil.getPath(gameInfo, dofusMaps)

        if (zaapWithDist == null && path == null) {
            error("No travel element found")
        }

        val travelDist = path?.size ?: Int.MAX_VALUE
        val zaapOk = if (zaapWithDist != null && zaapWithDist.second < travelDist - 1) {
            ZaapTowardTask(zaapWithDist.first).run(logItem, gameInfo)
        } else true
        return zaapOk && TravelTask(dofusMaps).run(logItem, gameInfo)
    }

    override fun onStarted(): String {
        val mapsStr = dofusMaps.map { it.getCoordinates() }
            .distinct()
            .joinToString(", ") { "(${it.x}; ${it.y})" }
        return "Reaching any map in [$mapsStr] ..."
    }
}