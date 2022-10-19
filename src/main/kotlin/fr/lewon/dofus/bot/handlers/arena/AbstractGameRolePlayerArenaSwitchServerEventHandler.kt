package fr.lewon.dofus.bot.handlers.arena

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.game.RetryUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

abstract class AbstractGameRolePlayerArenaSwitchServerEventHandler<T : NetworkMessage> : IEventHandler<T> {

    override fun onEventReceived(socketResult: T, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        RetryUtil.tryUntilSuccess(
            { GameSnifferUtil.updateNetwork() },
            { GameSnifferUtil.getConnections(gameInfo.character).size > 1 },
            5,
            { WaitUtil.sleep(500) }
        ) ?: error("Invalid connections : ${GameSnifferUtil.getConnections(gameInfo.character)}")
    }

    protected abstract fun isConnectionsValid(gameInfo: GameInfo, connections: List<DofusConnection>): Boolean

}