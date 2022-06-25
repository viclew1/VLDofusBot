package fr.lewon.dofus.bot.handlers.exchange

import fr.lewon.dofus.bot.gui2.main.metamob.util.MetamobMonstersUpdater
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.exchange.ExchangeLeaveMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.filemanagers.impl.MetamobConfigManager
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo
import fr.lewon.dofus.bot.util.network.info.TradeInfo

object ExchangeLeaveEventHandler : IEventHandler<ExchangeLeaveMessage> {
    override fun onEventReceived(socketResult: ExchangeLeaveMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        if (socketResult.success && MetamobConfigManager.readConfig().tradeAutoUpdate) {
            Thread { updateMetamob(gameInfo) }.start()
        } else {
            gameInfo.currentTradeInfo = TradeInfo()
        }
        gameInfo.inShop = false
    }

    @Synchronized
    private fun updateMetamob(gameInfo: GameInfo) {
        MetamobMonstersUpdater.addMonsters(gameInfo.currentTradeInfo.toAddItems)
        MetamobMonstersUpdater.removeMonsters(gameInfo.currentTradeInfo.toDeleteItems)
    }
}