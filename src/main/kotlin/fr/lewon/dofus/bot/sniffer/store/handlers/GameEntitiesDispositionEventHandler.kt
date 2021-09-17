package fr.lewon.dofus.bot.sniffer.store.handlers

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameEntitiesDispositionMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.ui.ConsoleLogger

object GameEntitiesDispositionEventHandler : EventHandler<GameEntitiesDispositionMessage> {

    override fun onEventReceived(socketResult: GameEntitiesDispositionMessage) {
        for (disposition in socketResult.dispositions) {
            GameInfo.fightBoard.createOrUpdateFighter(disposition.id, disposition.cellId)
        }
        ConsoleLogger.info("Fighters positions updated")
    }

}