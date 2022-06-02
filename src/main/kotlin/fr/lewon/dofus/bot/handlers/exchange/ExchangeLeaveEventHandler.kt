package fr.lewon.dofus.bot.handlers.exchange

import fr.lewon.dofus.bot.gui.metamobhelper.util.MetamobMonstersUpdater
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
            updateMetamob(gameInfo)
        } else {
            gameInfo.currentTradeInfo = TradeInfo()
        }
        gameInfo.inShop = false
    }

    private fun updateMetamob(gameInfo: GameInfo) {
        Thread {
            MetamobMonstersUpdater.addMonsters(gameInfo.currentTradeInfo.toAddItems)
            MetamobMonstersUpdater.removeMonsters(gameInfo.currentTradeInfo.toDeleteItems)
        }.start()
    }
}