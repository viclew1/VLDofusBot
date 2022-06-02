package fr.lewon.dofus.bot.handlers.exchange

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.exchange.ExchangeStartedBidBuyerMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object ExchangeStartedBidBuyerEventHandler : IEventHandler<ExchangeStartedBidBuyerMessage> {
    override fun onEventReceived(socketResult: ExchangeStartedBidBuyerMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.inShop = true
    }
}