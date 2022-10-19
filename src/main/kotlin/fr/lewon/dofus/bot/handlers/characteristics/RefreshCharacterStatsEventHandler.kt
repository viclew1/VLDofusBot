package fr.lewon.dofus.bot.handlers.characteristics

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight.RefreshCharacterStatsMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object RefreshCharacterStatsEventHandler : IEventHandler<RefreshCharacterStatsMessage> {

    override fun onEventReceived(socketResult: RefreshCharacterStatsMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        val fighterId = socketResult.fighterId
        val characteristics = socketResult.stats.characteristics.characteristics
        gameInfo.fightBoard.updateFighterCharacteristics(fighterId, characteristics)
        gameInfo.fightBoard.getFighterById(fighterId)?.invisibilityState = socketResult.stats.invisibilityState
    }
}