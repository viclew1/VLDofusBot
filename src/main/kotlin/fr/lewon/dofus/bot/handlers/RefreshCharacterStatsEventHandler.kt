package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.fight.RefreshCharacterStatsMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.logs.VldbLogger

object RefreshCharacterStatsEventHandler : EventHandler<RefreshCharacterStatsMessage> {

    override fun onEventReceived(socketResult: RefreshCharacterStatsMessage) {
        val fighterId = socketResult.fighterId
        val characteristics = socketResult.stats.characteristics.characteristics
        GameInfo.fightBoard.updateFighterCharacteristics(fighterId, characteristics)
        VldbLogger.info("Fighter [$fighterId] characteristics updated")
    }
}