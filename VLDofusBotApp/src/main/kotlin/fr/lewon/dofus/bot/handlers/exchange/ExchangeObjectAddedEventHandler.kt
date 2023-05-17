package fr.lewon.dofus.bot.handlers.exchange

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges.ExchangeObjectAddedMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object ExchangeObjectAddedEventHandler : IEventHandler<ExchangeObjectAddedMessage> {
    override fun onEventReceived(socketResult: ExchangeObjectAddedMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        if (socketResult.remote) {
            gameInfo.currentTradeInfo.toAddItems.add(socketResult.obj)
        } else {
            gameInfo.currentTradeInfo.toDeleteItems.add(socketResult.obj)
        }
    }
}