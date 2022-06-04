package fr.lewon.dofus.bot.handlers.movement

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.move.CurrentMapMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object CurrentMapEventHandler : IEventHandler<CurrentMapMessage> {
    override fun onEventReceived(socketResult: CurrentMapMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.updateCellData(socketResult.mapId)
    }
}