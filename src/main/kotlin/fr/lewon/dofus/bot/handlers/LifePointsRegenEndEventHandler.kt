package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.stats.LifePointsRegenEndMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object LifePointsRegenEndEventHandler : EventHandler<LifePointsRegenEndMessage> {
    override fun onEventReceived(socketResult: LifePointsRegenEndMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.hp = socketResult.lifePoints
        gameInfo.maxHp = socketResult.maxLifePoints
        gameInfo.updatePlayerFighter()
    }
}