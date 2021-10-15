package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.model.maps.MapInformation
import fr.lewon.dofus.bot.sniffer.model.messages.move.ZaapDestinationsMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager

object ZaapDestinationsEventHandler : EventHandler<ZaapDestinationsMessage> {

    override fun onEventReceived(socketResult: ZaapDestinationsMessage) {
        CharacterManager.updateZaapDestinations(socketResult.destinations.mapTo(ArrayList()) { tpDest ->
            MapInformation(tpDest.map.id, tpDest.map.subAreaId)
        })
        VldbLogger.debug("Received ${socketResult.destinations.size} Zaap destinations")
    }

}