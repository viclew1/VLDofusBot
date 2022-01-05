package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameFightShowFighterMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameFightShowFighterEventHandler : EventHandler<GameFightShowFighterMessage> {

    override fun onEventReceived(socketResult: GameFightShowFighterMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        val fighterId = socketResult.informations.contextualId
        val cellId = socketResult.informations.spawnInfo.informations.disposition.cellId
        val characteristics = socketResult.informations.stats.characteristics.characteristics
        val teamId = socketResult.informations.spawnInfo.teamId
        gameInfo.fightBoard.createOrUpdateFighter(fighterId, cellId, teamId)
        gameInfo.fightBoard.updateFighterCharacteristics(fighterId, characteristics)
        gameInfo.logger.debug("Fighter [$fighterId] characteristics and position updated : cell $cellId")
    }

}