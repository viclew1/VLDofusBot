package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.model.characters.MapInformation
import fr.lewon.dofus.bot.sniffer.model.messages.move.ZaapDestinationsMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.filemanagers.DTBCharacterManager
import fr.lewon.dofus.bot.util.logs.VldbLogger

object ZaapDestinationsEventHandler : EventHandler<ZaapDestinationsMessage> {

    override fun onEventReceived(socketResult: ZaapDestinationsMessage) {
        DTBCharacterManager.updateZaapDestinations(socketResult.destinations.mapTo(ArrayList()) { tpDest ->
            MapInformation(tpDest.map.id, tpDest.map.subAreaId)
        })
        VldbLogger.info("Received ${socketResult.destinations.size} Zaap destinations")
    }

}