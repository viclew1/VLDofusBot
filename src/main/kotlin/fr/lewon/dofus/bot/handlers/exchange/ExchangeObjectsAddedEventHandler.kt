package fr.lewon.dofus.bot.handlers.exchange

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges.ExchangeObjectsAddedMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object ExchangeObjectsAddedEventHandler : IEventHandler<ExchangeObjectsAddedMessage> {
    override fun onEventReceived(socketResult: ExchangeObjectsAddedMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        if (socketResult.remote) {
            gameInfo.currentTradeInfo.toAddItems.addAll(socketResult.obj)
        } else {
            gameInfo.currentTradeInfo.toDeleteItems.addAll(socketResult.obj)
        }
    }
}