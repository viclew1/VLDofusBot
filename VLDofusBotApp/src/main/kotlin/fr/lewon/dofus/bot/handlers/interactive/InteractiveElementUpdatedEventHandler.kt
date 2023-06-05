package fr.lewon.dofus.bot.handlers.interactive

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.interactive.InteractiveElementUpdatedMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object InteractiveElementUpdatedEventHandler : IEventHandler<InteractiveElementUpdatedMessage> {
    override fun onEventReceived(socketResult: InteractiveElementUpdatedMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        val updatedElement = socketResult.interactiveElement
        val interactiveElements = gameInfo.interactiveElements.toMutableList()
        val index = interactiveElements.indexOfFirst { it.elementId == updatedElement.elementId }
        if (index >= 0) {
            interactiveElements[index] = updatedElement
            gameInfo.interactiveElements = interactiveElements
        }
    }
}