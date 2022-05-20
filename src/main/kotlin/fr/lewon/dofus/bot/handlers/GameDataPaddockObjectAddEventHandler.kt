package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.breeding.GameDataPaddockObjectAddMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameDataPaddockObjectAddEventHandler : EventHandler<GameDataPaddockObjectAddMessage> {

    override fun onEventReceived(socketResult: GameDataPaddockObjectAddMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        val paddockItem = socketResult.paddockItemDescription
        gameInfo.paddockItemByCell[paddockItem.cellId] = paddockItem
    }

}