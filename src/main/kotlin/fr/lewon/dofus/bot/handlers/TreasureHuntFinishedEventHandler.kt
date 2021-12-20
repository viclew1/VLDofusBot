package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.treasurehunt.TreasureHuntFinishedMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object TreasureHuntFinishedEventHandler : EventHandler<TreasureHuntFinishedMessage> {

    override fun onEventReceived(socketResult: TreasureHuntFinishedMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.treasureHunt = null
        gameInfo.logger.debug("Treasure hunt finished")
    }
}