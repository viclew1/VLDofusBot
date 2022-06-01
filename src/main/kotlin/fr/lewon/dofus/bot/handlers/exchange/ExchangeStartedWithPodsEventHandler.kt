package fr.lewon.dofus.bot.handlers.exchange

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.exchange.ExchangeStartedWithPodsMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import fr.lewon.dofus.bot.util.network.info.TradeInfo

object ExchangeStartedWithPodsEventHandler : IEventHandler<ExchangeStartedWithPodsMessage> {
    override fun onEventReceived(socketResult: ExchangeStartedWithPodsMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.currentTradeInfo = TradeInfo()
    }
}