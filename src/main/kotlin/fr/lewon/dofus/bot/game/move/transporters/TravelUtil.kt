package fr.lewon.dofus.bot.game.move.transporters

import fr.lewon.dofus.bot.model.maps.DofusCoordinates

object TravelUtil {

    fun getZaaps(altWorld: Boolean = false): List<Zaap> {
        return Zaap.values().filter {
            it.isAltWorld() == altWorld
        }
    }

    fun getTransporters(altWorld: Boolean = false): List<ITransporter> {
        return listOf<ITransporter>(
            *OtomaiTransporter.values(),
            *FrigostTransporter.values()
        ).filter { it.isAltWorld() == altWorld }
    }

    fun <T : ITravelElement> getClosestTravelElement(travelElements: List<T>, coordinates: DofusCoordinates): T? {
        return travelElements.minBy { it.getCoordinates().distanceTo(coordinates) }
    }

}