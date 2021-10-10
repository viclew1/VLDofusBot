package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameFightTurnStartPlayingMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.logs.VldbLogger

object GameFightTurnStartPlayingEventHandler : EventHandler<GameFightTurnStartPlayingMessage> {

    override fun onEventReceived(socketResult: GameFightTurnStartPlayingMessage) {
        VldbLogger.info("Turn starting")
    }
}