package fr.lewon.dofus.bot.handlers.exchange

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.exchange.ExchangeObjectRemovedMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object ExchangeObjectRemovedEventHandler : IEventHandler<ExchangeObjectRemovedMessage> {
    override fun onEventReceived(socketResult: ExchangeObjectRemovedMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        if (socketResult.remote) {
            gameInfo.currentTradeInfo.toAddItems.removeIf { it.objectUID == socketResult.objectUID }
        } else {
            gameInfo.currentTradeInfo.toDeleteItems.removeIf { it.objectUID == socketResult.objectUID }
        }
    }
}