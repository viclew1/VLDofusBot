package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameActionFightMultipleSummonMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameActionFightMultipleSummonEventHandler : EventHandler<GameActionFightMultipleSummonMessage> {
    override fun onEventReceived(socketResult: GameActionFightMultipleSummonMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        socketResult.summons.forEach {
            it.summons.forEach { basicSpawnInfo ->
                val fighterId = basicSpawnInfo.informations.contextualId
                val cellId = basicSpawnInfo.informations.disposition.cellId
                val teamId = basicSpawnInfo.teamId
                gameInfo.fightBoard.summonFighter(fighterId, cellId, teamId)
                gameInfo.logger.debug("New summon : [$fighterId]")
            }
        }
    }
}