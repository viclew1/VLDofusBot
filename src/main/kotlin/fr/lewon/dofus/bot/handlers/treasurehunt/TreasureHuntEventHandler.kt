package fr.lewon.dofus.bot.handlers.treasurehunt

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.treasurehunt.TreasureHuntMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object TreasureHuntEventHandler : IEventHandler<TreasureHuntMessage> {

    override fun onEventReceived(socketResult: TreasureHuntMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.treasureHunt = socketResult
    }

}