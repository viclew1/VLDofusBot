package fr.lewon.dofus.bot.handlers.breeding

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.mount.GameDataPaddockObjectAddMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameDataPaddockObjectAddEventHandler : IEventHandler<GameDataPaddockObjectAddMessage> {

    override fun onEventReceived(socketResult: GameDataPaddockObjectAddMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        val paddockItem = socketResult.paddockItemDescription
        gameInfo.paddockItemByCell[paddockItem.cellId] = paddockItem
    }

}