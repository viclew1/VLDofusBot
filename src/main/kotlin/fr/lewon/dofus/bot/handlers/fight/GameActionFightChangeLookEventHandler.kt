package fr.lewon.dofus.bot.handlers.fight

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameActionFightChangeLookMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameActionFightChangeLookEventHandler : IEventHandler<GameActionFightChangeLookMessage> {

    override fun onEventReceived(socketResult: GameActionFightChangeLookMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.fightBoard.getFighterById(socketResult.targetId)?.bonesId = socketResult.entityLook.bonesId
    }

}