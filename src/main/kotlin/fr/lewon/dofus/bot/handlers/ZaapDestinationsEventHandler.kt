package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.move.ZaapDestinationsMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object ZaapDestinationsEventHandler : EventHandler<ZaapDestinationsMessage> {

    override fun onEventReceived(socketResult: ZaapDestinationsMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.logger.debug("Received ${socketResult.destinations.size} Zaap destinations")
    }

}