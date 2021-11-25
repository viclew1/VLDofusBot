package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.game.move.transporters.TravelUtil
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
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
        val closestZaap = TravelUtil.getClosestTravelElement(zaaps, destinationCoordinates)
        val closestTransporter = TravelUtil.getClosestTravelElement(transporters, destinationCoordinates)

        val transporterDist = closestTransporter?.getCoordinates()?.distanceTo(destinationCoordinates) ?: Int.MAX_VALUE
        val zaapDist = closestZaap?.getCoordinates()?.distanceTo(destinationCoordinates) ?: Int.MAX_VALUE

        when {
            closestTransporter != null && transporterDist < zaapDist ->
                TransportTowardTask(closestTransporter).run(logItem, gameInfo, cancellationToken)
            closestZaap != null ->
                ZaapTowardTask(closestZaap).run(logItem, gameInfo, cancellationToken)
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