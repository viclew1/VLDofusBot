package fr.lewon.dofus.bot.sniffer.store.handlers

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.treasurehunt.TreasureHuntFinishedMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.ui.ConsoleLogger

object TreasureHuntFinishedEventHandler : EventHandler<TreasureHuntFinishedMessage> {

    override fun onEventReceived(socketResult: TreasureHuntFinishedMessage) {
        GameInfo.treasureHunt = null
        ConsoleLogger.info("Treasure hunt finished")
    }
}