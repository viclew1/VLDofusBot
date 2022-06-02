package fr.lewon.dofus.bot.handlers.exchange

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.exchange.ExchangeStartOkHumanVendorMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object ExchangeStartOkHumanVendorEventHandler : IEventHandler<ExchangeStartOkHumanVendorMessage> {
    override fun onEventReceived(socketResult: ExchangeStartOkHumanVendorMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.inShop = true
    }
}