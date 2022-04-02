package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameActionFightLifeAndShieldPointsLostMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameActionFightLifeAndShieldPointsLostEventHandler :
    EventHandler<GameActionFightLifeAndShieldPointsLostMessage> {

    override fun onEventReceived(
        socketResult: GameActionFightLifeAndShieldPointsLostMessage,
        connection: DofusConnection
    ) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        GameActionFightLifePointsLostEventHandler.onEventReceived(socketResult, connection)
        gameInfo.fightBoard.getFighterById(socketResult.targetId)?.let {
            it.shield -= socketResult.shieldLoss
        }
    }

}