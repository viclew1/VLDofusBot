package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.breeding.GameDataPaddockObjectListAddMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameDataPaddockObjectListAddEventHandler : EventHandler<GameDataPaddockObjectListAddMessage> {

    override fun onEventReceived(socketResult: GameDataPaddockObjectListAddMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        for (paddockItem in socketResult.paddockItemsDescription) {
            gameInfo.paddockItemByCell[paddockItem.cellId] = paddockItem
        }
    }

}