package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.misc.GameContextRemoveElementMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameContextRemoveElementEventHandler : IEventHandler<GameContextRemoveElementMessage> {
    override fun onEventReceived(socketResult: GameContextRemoveElementMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.monsterInfoByEntityId.remove(socketResult.id)
        gameInfo.entityIdByNpcId.entries.removeIf { it.value == socketResult.id }
        gameInfo.entityPositionsOnMapByEntityId.entries.removeIf { it.key == socketResult.id }
    }
}