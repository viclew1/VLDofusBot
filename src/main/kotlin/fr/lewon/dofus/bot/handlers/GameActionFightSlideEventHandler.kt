package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameActionFightSlideMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameActionFightSlideEventHandler : EventHandler<GameActionFightSlideMessage> {

    override fun onEventReceived(socketResult: GameActionFightSlideMessage, snifferId: Long) {
        val gameInfo = GameSnifferUtil.getGameInfoBySnifferId(snifferId)
        val fighterId = socketResult.targetId
        val cellId = socketResult.endCellId
        gameInfo.fightBoard.move(fighterId, cellId)
        VldbLogger.debug("Fighter [$fighterId] slided to cell [$cellId]")
    }

}