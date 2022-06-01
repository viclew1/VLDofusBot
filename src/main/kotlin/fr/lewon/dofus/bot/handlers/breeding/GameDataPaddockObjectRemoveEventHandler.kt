package fr.lewon.dofus.bot.handlers.breeding

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.breeding.GameDataPaddockObjectRemoveMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameDataPaddockObjectRemoveEventHandler : IEventHandler<GameDataPaddockObjectRemoveMessage> {
    override fun onEventReceived(socketResult: GameDataPaddockObjectRemoveMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.paddockItemByCell.remove(socketResult.cellId)
    }
}