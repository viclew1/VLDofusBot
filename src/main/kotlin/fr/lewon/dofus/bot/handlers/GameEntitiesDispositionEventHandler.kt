package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameEntitiesDispositionMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameEntitiesDispositionEventHandler : EventHandler<GameEntitiesDispositionMessage> {

    override fun onEventReceived(socketResult: GameEntitiesDispositionMessage, snifferId: Long) {
        val gameInfo = GameSnifferUtil.getGameInfoBySnifferId(snifferId)
        for (disposition in socketResult.dispositions) {
            VldbLogger.debug("Fighter ${disposition.id} position updated : ${disposition.cellId}")
            gameInfo.fightBoard.createOrUpdateFighter(disposition.id, disposition.cellId)
        }
    }

}