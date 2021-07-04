package fr.lewon.dofus.bot.sniffer.store.handlers

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.TreasureHuntFinishedMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler

object TreasureHuntEndEventHandler : EventHandler<TreasureHuntFinishedMessage> {

    override fun onEventReceived(socketResult: TreasureHuntFinishedMessage) {
        println("HUNT END")
        GameInfo.treasureHunt = null
    }
}