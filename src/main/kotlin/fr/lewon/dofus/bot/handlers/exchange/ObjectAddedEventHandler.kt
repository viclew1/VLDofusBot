package fr.lewon.dofus.bot.handlers.exchange

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.items.ObjectAddedMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.external.metamob.MetamobMonstersHelper
import fr.lewon.dofus.bot.util.filemanagers.impl.MetamobConfigManager
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object ObjectAddedEventHandler : IEventHandler<ObjectAddedMessage> {
    override fun onEventReceived(socketResult: ObjectAddedMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        if (gameInfo.inShop && MetamobConfigManager.readConfig().shopAutoUpdate) {
            if (MetamobMonstersHelper.isMetamobConfigured()) {
                Thread {
                    MetamobMonstersHelper.addMonsters(listOf(socketResult.obj))
                }.start()
            }
        }
    }
}