package fr.lewon.dofus.bot.sniffer.store.handlers

import fr.lewon.dofus.bot.model.characters.MapInformation
import fr.lewon.dofus.bot.sniffer.model.messages.move.ZaapDestinationsMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.filemanagers.DTBCharacterManager
import fr.lewon.dofus.bot.util.ui.ConsoleLogger

object ZaapDestinationsEventHandler : EventHandler<ZaapDestinationsMessage> {

    override fun onEventReceived(socketResult: ZaapDestinationsMessage) {
        DTBCharacterManager.updateZaapDestinations(socketResult.destinations.mapTo(ArrayList()) { tpDest ->
            MapInformation(tpDest.map.id, tpDest.map.subAreaId)
        })
        ConsoleLogger.info("Received ${socketResult.destinations.size} Zaap destinations")
    }

}