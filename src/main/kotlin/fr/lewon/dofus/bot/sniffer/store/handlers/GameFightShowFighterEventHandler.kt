package fr.lewon.dofus.bot.sniffer.store.handlers

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameFightShowFighterMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.ui.ConsoleLogger

object GameFightShowFighterEventHandler : EventHandler<GameFightShowFighterMessage> {

    override fun onEventReceived(socketResult: GameFightShowFighterMessage) {
        val fighterId = socketResult.informations.contextualId
        val cellId = socketResult.informations.spawnInfo.informations.disposition.cellId
        val characteristics = socketResult.informations.stats.characteristics.characteristics
        GameInfo.fightBoard.createOrUpdateFighter(fighterId, cellId)
        GameInfo.fightBoard.updateFighterCharacteristics(fighterId, characteristics)
        ConsoleLogger.info("Fighter [$fighterId] characteristics and position updated")
    }

}