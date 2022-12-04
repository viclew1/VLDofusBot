package fr.lewon.dofus.bot.handlers.exchange

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges.ExchangeLeaveMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.external.metamob.MetamobMonstersHelper
import fr.lewon.dofus.bot.util.filemanagers.impl.MetamobConfigManager
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import fr.lewon.dofus.bot.util.network.info.TradeInfo

object ExchangeLeaveEventHandler : IEventHandler<ExchangeLeaveMessage> {
    override fun onEventReceived(socketResult: ExchangeLeaveMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        if (socketResult.success && MetamobConfigManager.readConfig().tradeAutoUpdate) {
            if (MetamobMonstersHelper.isMetamobConfigured()) {
                Thread {
                    MetamobMonstersHelper.addAndRemoveMonsters(
                        gameInfo.currentTradeInfo.toAddItems,
                        gameInfo.currentTradeInfo.toDeleteItems
                    )
                }.start()
            }
        } else {
            gameInfo.currentTradeInfo = TradeInfo()
        }
        gameInfo.inShop = false
    }

}