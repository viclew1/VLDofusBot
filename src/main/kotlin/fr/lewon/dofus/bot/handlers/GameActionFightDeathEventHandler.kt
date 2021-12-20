package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameActionFightDeathMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameActionFightDeathEventHandler : EventHandler<GameActionFightDeathMessage> {

    override fun onEventReceived(socketResult: GameActionFightDeathMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.fightBoard.killFighter(socketResult.targetId)
        gameInfo.logger.debug("Fighter [${socketResult.targetId}] has been killed by fighter [${socketResult.sourceId}]")
    }

}