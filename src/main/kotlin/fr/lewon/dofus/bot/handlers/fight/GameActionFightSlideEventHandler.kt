package fr.lewon.dofus.bot.handlers.fight

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.actions.fight.GameActionFightSlideMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameActionFightSlideEventHandler : IEventHandler<GameActionFightSlideMessage> {

    override fun onEventReceived(socketResult: GameActionFightSlideMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        val fighterId = socketResult.targetId
        val cellId = socketResult.endCellId
        gameInfo.fightBoard.move(fighterId, cellId)
    }

}