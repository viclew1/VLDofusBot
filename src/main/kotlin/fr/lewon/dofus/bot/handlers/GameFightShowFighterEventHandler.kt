package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameFightShowFighterMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameFightShowFighterEventHandler : EventHandler<GameFightShowFighterMessage> {

    override fun onEventReceived(socketResult: GameFightShowFighterMessage, snifferId: Long) {
        val gameInfo = GameSnifferUtil.getGameInfoBySnifferId(snifferId)
        val fighterId = socketResult.informations.contextualId
        val cellId = socketResult.informations.spawnInfo.informations.disposition.cellId
        val characteristics = socketResult.informations.stats.characteristics.characteristics
        gameInfo.fightBoard.createOrUpdateFighter(fighterId, cellId)
        gameInfo.fightBoard.updateFighterCharacteristics(fighterId, characteristics)
        VldbLogger.debug("Fighter [$fighterId] characteristics and position updated : cell $cellId")
    }

}