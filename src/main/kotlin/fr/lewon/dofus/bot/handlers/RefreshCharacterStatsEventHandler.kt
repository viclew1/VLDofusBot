package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.sniffer.model.messages.fight.RefreshCharacterStatsMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object RefreshCharacterStatsEventHandler : EventHandler<RefreshCharacterStatsMessage> {

    override fun onEventReceived(socketResult: RefreshCharacterStatsMessage, snifferId: Long) {
        val gameInfo = GameSnifferUtil.getGameInfoBySnifferId(snifferId)
        val fighterId = socketResult.fighterId
        val characteristics = socketResult.stats.characteristics.characteristics
        gameInfo.fightBoard.updateFighterCharacteristics(fighterId, characteristics)
        VldbLogger.debug("Fighter [$fighterId] characteristics updated")
    }
}