package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.game.move.transporters.TravelUtil
import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.util.logs.LogItem

open class ReachMapTask(private val dofusMap: DofusMap) : DofusBotTask<DofusMap>() {

    override fun execute(logItem: LogItem): DofusMap {
        if (dofusMap == GameInfo.currentMap) {
            return dofusMap
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
                TransportTowardTask(closestTransporter).run(logItem)
            closestZaap != null ->
                ZaapTowardTask(closestZaap).run(logItem)
            else ->
                error("No travel element found")
        }
        return TravelTask(destinationCoordinates).run(logItem)
    }

    override fun onStarted(): String {
        val coordinates = dofusMap.getCoordinates()
        return "Reaching map [${coordinates.x}, ${coordinates.y}] ..."
    }
}