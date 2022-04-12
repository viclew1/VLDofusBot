package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameActionFightExchangePositionsMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameActionFightExchangePositionsEventHandler : EventHandler<GameActionFightExchangePositionsMessage> {

    override fun onEventReceived(socketResult: GameActionFightExchangePositionsMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        val caster = gameInfo.fightBoard.getFighter(socketResult.casterCellId)
        val target = gameInfo.fightBoard.getFighter(socketResult.targetCellId)
        caster?.let { gameInfo.fightBoard.move(it, socketResult.targetCellId) }
        target?.let { gameInfo.fightBoard.move(it, socketResult.casterCellId) }
    }
}