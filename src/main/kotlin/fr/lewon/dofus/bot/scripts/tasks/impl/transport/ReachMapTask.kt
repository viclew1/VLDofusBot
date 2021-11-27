package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.game.move.transporters.TravelUtil
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.TravelTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.TravelToCoordinatesTask
import fr.lewon.dofus.bot.util.network.GameInfo

open class ReachMapTask(private val dofusMap: DofusMap) : DofusBotTask<Boolean>() {

    override fun execute(logItem: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken): Boolean {
        if (dofusMap == gameInfo.currentMap) {
            return true
        }

        val zaaps = TravelUtil.getZaaps(dofusMap.isAltWorld())
        val transporters = TravelUtil.getTransporters(dofusMap.isAltWorld())

        val destinationCoordinates = dofusMap.getCoordinates()
        val zaap = TravelUtil.getClosestTravelElement(zaaps, destinationCoordinates)
        val transporter = TravelUtil.getClosestTravelElement(transporters, destinationCoordinates)
        val path = TravelUtil.getPath(gameInfo, dofusMap)

        val transporterDist = transporter?.getCoordinates()?.distanceTo(destinationCoordinates)
            ?.plus(transporter.getClosestZaap().getCoordinates().distanceTo(transporter.getTransporterCoordinates()))
            ?: Int.MAX_VALUE
        val zaapDist = zaap?.getCoordinates()?.distanceTo(destinationCoordinates) ?: Int.MAX_VALUE
        val travelDist = path?.size ?: Int.MAX_VALUE

        val minDist = minOf(transporterDist, zaapDist, travelDist)

        when {
            path != null && minDist == travelDist ->
                TravelTask(listOf(dofusMap)).run(logItem, gameInfo, cancellationToken)
            transporter != null && transporterDist == minDist ->
                TransportTowardTask(transporter).run(logItem, gameInfo, cancellationToken)
            zaap != null && zaapDist == minDist ->
                ZaapTowardTask(zaap).run(logItem, gameInfo, cancellationToken)
            else ->
                error("No travel element found")
        }
        return TravelToCoordinatesTask(destinationCoordinates).run(logItem, gameInfo, cancellationToken)
    }

    override fun onStarted(): String {
        val coordinates = dofusMap.getCoordinates()
        return "Reaching map [${coordinates.x}, ${coordinates.y}] ..."
    }
}