package fr.lewon.dofus.bot.handlers.exchange

import fr.lewon.dofus.bot.gui.metamobhelper.util.MetamobMonstersUpdater
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.misc.ObjectAddedMessage
import fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.`object`.ObjectItem
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.filemanagers.impl.MetamobConfigManager
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object ObjectAddedEventHandler : IEventHandler<ObjectAddedMessage> {
    override fun onEventReceived(socketResult: ObjectAddedMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        if (gameInfo.inShop && MetamobConfigManager.readConfig().shopAutoUpdate) {
            updateMetamob(socketResult.objectItem)
        }
    }

    private fun updateMetamob(objectItem: ObjectItem) {
        Thread {
            MetamobMonstersUpdater.addMonsters(listOf(objectItem))
        }.start()
    }
}