package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameActionFightLifePointsLostMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameActionFightLifePointsLostEventHandler : EventHandler<GameActionFightLifePointsLostMessage> {

    override fun onEventReceived(socketResult: GameActionFightLifePointsLostMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.fightBoard.getFighterById(socketResult.targetId)?.let {
            it.hpLost += socketResult.loss
        }
    }

}