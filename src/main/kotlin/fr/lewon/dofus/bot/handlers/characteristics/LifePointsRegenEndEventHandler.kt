package fr.lewon.dofus.bot.handlers.characteristics

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.character.stats.LifePointsRegenEndMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object LifePointsRegenEndEventHandler : IEventHandler<LifePointsRegenEndMessage> {
    override fun onEventReceived(socketResult: LifePointsRegenEndMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.hp = socketResult.lifePoints
        gameInfo.maxHp = socketResult.maxLifePoints
        gameInfo.updatePlayerFighter()
    }
}