package fr.lewon.dofus.bot.handlers.exchange

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.items.ExchangeObjectsRemovedMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object ExchangeObjectsRemovedEventHandler : IEventHandler<ExchangeObjectsRemovedMessage> {
    override fun onEventReceived(socketResult: ExchangeObjectsRemovedMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        if (socketResult.remote) {
            gameInfo.currentTradeInfo.toAddItems.removeIf { socketResult.objectUID.contains(it.objectUID) }
        } else {
            gameInfo.currentTradeInfo.toDeleteItems.removeIf { socketResult.objectUID.contains(it.objectUID) }
        }
    }
}