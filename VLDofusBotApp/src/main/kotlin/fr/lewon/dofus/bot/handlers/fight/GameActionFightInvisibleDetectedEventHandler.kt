package fr.lewon.dofus.bot.handlers.fight

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.actions.fight.GameActionFightInvisibleDetectedMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameActionFightInvisibleDetectedEventHandler : IEventHandler<GameActionFightInvisibleDetectedMessage> {

    override fun onEventReceived(socketResult: GameActionFightInvisibleDetectedMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.fightBoard.move(socketResult.targetId, socketResult.cellId)
    }
}