package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameActionFightSlideMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler

object GameActionFightSlideEventHandler : EventHandler<GameActionFightSlideMessage> {

    override fun onEventReceived(socketResult: GameActionFightSlideMessage) {
        val fighterId = socketResult.targetId
        val cellId = socketResult.endCellId
        GameInfo.fightBoard.move(fighterId, cellId)
        VldbLogger.debug("Fighter [$fighterId] slided to cell [$cellId]")
    }

}