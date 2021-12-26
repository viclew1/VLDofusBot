package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameActionFightLifePointsGainMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameActionFightLifePointsGainEventHandler : EventHandler<GameActionFightLifePointsGainMessage> {

    override fun onEventReceived(socketResult: GameActionFightLifePointsGainMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.fightBoard.getFighterById(socketResult.targetId)?.let {
            it.hpHealed += socketResult.delta
        }
    }

}