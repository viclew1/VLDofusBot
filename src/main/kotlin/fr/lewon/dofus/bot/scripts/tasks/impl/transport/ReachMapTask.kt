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
        val zaaps = TravelUtil.getAllZaapMaps()
        val transporters = TravelUtil.getTransporters()

        val zaapWithDist = TravelUtil.getClosestZaap(gameInfo, dofusMaps)
        val transporterWithDist = TravelUtil.getClosestTransporter(gameInfo, transporters, dofusMaps)
        val transporterToZaapDist = transporterWithDist?.first?.let {
            TravelUtil.getPath(transporterWithDist.first.getTransporterMap(), 1, zaaps, gameInfo)?.size
        } ?: Int.MAX_VALUE
        val path = TravelUtil.getPath(gameInfo, dofusMaps)


        val transporterDist = transporterWithDist?.second?.plus(transporterToZaapDist) ?: Int.MAX_VALUE
        val zaapDist = zaapWithDist?.second ?: Int.MAX_VALUE
        val travelDist = path?.size ?: Int.MAX_VALUE

        val minDist = minOf(transporterDist, zaapDist, travelDist)

        val transportResult = when {
            path != null && minDist == travelDist ->
                true
            transporterWithDist != null && transporterDist == minDist ->
                TransportTowardTask(transporterWithDist.first).run(logItem, gameInfo)
            zaapWithDist != null && zaapDist == minDist ->
                ZaapTowardTask(zaapWithDist.first).run(logItem, gameInfo)
            else ->
                error("No travel element found")
        }
        return transportResult && TravelTask(dofusMaps).run(logItem, gameInfo)
    }

    override fun onStarted(): String {
        val mapsStr = dofusMaps.map { it.getCoordinates() }
            .distinct()
            .joinToString(", ") { "(${it.x}; ${it.y})" }
        return "Reaching any map in [$mapsStr] ..."
    }
}