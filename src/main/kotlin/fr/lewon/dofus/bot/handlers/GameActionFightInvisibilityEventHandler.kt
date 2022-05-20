package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameActionFightInvisibilityMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameActionFightInvisibilityEventHandler : EventHandler<GameActionFightInvisibilityMessage> {

    override fun onEventReceived(socketResult: GameActionFightInvisibilityMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.fightBoard.getFighterById(socketResult.targetId)?.let {
            it.invisibilityState = socketResult.state
        }
    }
}