package fr.lewon.dofus.bot.handlers.treasurehunt

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.treasureHunt.TreasureHuntFinishedMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object TreasureHuntFinishedEventHandler : IEventHandler<TreasureHuntFinishedMessage> {

    override fun onEventReceived(socketResult: TreasureHuntFinishedMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.treasureHunt = null
    }
}