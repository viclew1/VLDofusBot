package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameActionFightSlideMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.logs.VldbLogger

object GameActionFightSlideEventHandler : EventHandler<GameActionFightSlideMessage> {

    override fun onEventReceived(socketResult: GameActionFightSlideMessage) {
        val fighterId = socketResult.targetId
        val cellId = socketResult.endCellId
        GameInfo.fightBoard.move(fighterId, cellId)
        VldbLogger.info("Fighter [$fighterId] slided to cell [$cellId]")
    }

}