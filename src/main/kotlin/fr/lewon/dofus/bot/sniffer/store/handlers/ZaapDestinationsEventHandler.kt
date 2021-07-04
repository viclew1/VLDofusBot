package fr.lewon.dofus.bot.sniffer.store.handlers

import fr.lewon.dofus.bot.model.characters.MapInformation
import fr.lewon.dofus.bot.sniffer.model.messages.ZaapDestinationsMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.filemanagers.DTBCharacterManager

object ZaapDestinationsEventHandler : EventHandler<ZaapDestinationsMessage> {

    override fun onEventReceived(socketResult: ZaapDestinationsMessage) {
        DTBCharacterManager.updateZaapDestinations(socketResult.destinations.mapTo(ArrayList()) { tpDest ->
            MapInformation(tpDest.map.id, tpDest.map.subAreaId)
        })
        println("RECEIVED ${socketResult.destinations.size} ZAAP DESTINATIONS")
    }

}