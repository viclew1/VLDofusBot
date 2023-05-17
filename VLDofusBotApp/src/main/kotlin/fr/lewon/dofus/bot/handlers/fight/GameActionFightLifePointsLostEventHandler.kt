package fr.lewon.dofus.bot.handlers.fight

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.actions.fight.GameActionFightLifePointsLostMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameActionFightLifePointsLostEventHandler : IEventHandler<GameActionFightLifePointsLostMessage> {

    override fun onEventReceived(socketResult: GameActionFightLifePointsLostMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.fightBoard.getFighterById(socketResult.targetId)?.let {
            it.hpLost += socketResult.loss
            it.maxHp -= socketResult.permanentDamages
        }
    }

}