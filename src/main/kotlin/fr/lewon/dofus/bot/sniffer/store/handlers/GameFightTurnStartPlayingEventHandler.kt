package fr.lewon.dofus.bot.sniffer.store.handlers

import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameFightTurnStartPlayingMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.ui.ConsoleLogger

object GameFightTurnStartPlayingEventHandler : EventHandler<GameFightTurnStartPlayingMessage> {

    override fun onEventReceived(socketResult: GameFightTurnStartPlayingMessage) {
        ConsoleLogger.info("Turn starting")
    }
}