package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameFightTurnStartPlayingMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler

object GameFightTurnStartPlayingEventHandler : EventHandler<GameFightTurnStartPlayingMessage> {

    override fun onEventReceived(socketResult: GameFightTurnStartPlayingMessage, snifferId: Long) {
        VldbLogger.debug("Turn starting")
    }
}