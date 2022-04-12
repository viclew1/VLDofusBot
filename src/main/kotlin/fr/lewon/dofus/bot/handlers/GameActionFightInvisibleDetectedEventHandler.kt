package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameActionFightInvisibleDetectedMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameActionFightInvisibleDetectedEventHandler : EventHandler<GameActionFightInvisibleDetectedMessage> {

    override fun onEventReceived(socketResult: GameActionFightInvisibleDetectedMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.fightBoard.move(socketResult.targetId, socketResult.cellId)
    }
}