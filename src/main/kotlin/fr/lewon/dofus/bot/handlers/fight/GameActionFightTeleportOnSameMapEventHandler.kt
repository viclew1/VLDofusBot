package fr.lewon.dofus.bot.handlers.fight

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.actions.fight.GameActionFightTeleportOnSameMapMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameActionFightTeleportOnSameMapEventHandler : IEventHandler<GameActionFightTeleportOnSameMapMessage> {

    override fun onEventReceived(socketResult: GameActionFightTeleportOnSameMapMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        val fighterId = socketResult.targetId
        val cellId = socketResult.cellId
        gameInfo.fightBoard.move(fighterId, cellId)
    }

}