package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.treasurehunt.TreasureHuntFinishedMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler

object TreasureHuntFinishedEventHandler : EventHandler<TreasureHuntFinishedMessage> {

    override fun onEventReceived(socketResult: TreasureHuntFinishedMessage) {
        GameInfo.treasureHunt = null
        VldbLogger.debug("Treasure hunt finished")
    }
}