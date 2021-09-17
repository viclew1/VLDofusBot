package fr.lewon.dofus.bot.sniffer.store.handlers

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameActionFightSlideMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.ui.ConsoleLogger

object GameActionFightSlideEventHandler : EventHandler<GameActionFightSlideMessage> {

    override fun onEventReceived(socketResult: GameActionFightSlideMessage) {
        val fighterId = socketResult.targetId
        val cellId = socketResult.endCellId
        GameInfo.fightBoard.move(fighterId, cellId)
        ConsoleLogger.info("Fighter [$fighterId] slided to cell [$cellId]")
    }

}