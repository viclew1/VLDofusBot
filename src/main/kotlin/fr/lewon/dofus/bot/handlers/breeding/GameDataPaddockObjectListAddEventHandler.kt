package fr.lewon.dofus.bot.handlers.breeding

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.mount.GameDataPaddockObjectListAddMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameDataPaddockObjectListAddEventHandler : IEventHandler<GameDataPaddockObjectListAddMessage> {

    override fun onEventReceived(socketResult: GameDataPaddockObjectListAddMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        for (paddockItem in socketResult.paddockItemDescription) {
            gameInfo.paddockItemByCell[paddockItem.cellId] = paddockItem
        }
    }

}