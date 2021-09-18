package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameEntitiesDispositionMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.logs.VldbLogger

object GameEntitiesDispositionEventHandler : EventHandler<GameEntitiesDispositionMessage> {

    override fun onEventReceived(socketResult: GameEntitiesDispositionMessage) {
        for (disposition in socketResult.dispositions) {
            GameInfo.fightBoard.createOrUpdateFighter(disposition.id, disposition.cellId)
        }
        VldbLogger.info("Fighters positions updated")
    }

}