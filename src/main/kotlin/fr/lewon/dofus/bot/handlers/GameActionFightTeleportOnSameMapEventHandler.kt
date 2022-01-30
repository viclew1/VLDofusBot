package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameActionFightTeleportOnSameMapMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameActionFightTeleportOnSameMapEventHandler : EventHandler<GameActionFightTeleportOnSameMapMessage> {

    override fun onEventReceived(socketResult: GameActionFightTeleportOnSameMapMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        val fighterId = socketResult.targetId
        val cellId = socketResult.cellId
        gameInfo.fightBoard.move(fighterId, cellId)
    }

}