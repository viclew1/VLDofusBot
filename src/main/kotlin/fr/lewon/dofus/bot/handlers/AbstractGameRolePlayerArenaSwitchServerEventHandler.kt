package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.INetworkMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.game.RetryUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.GameInfo
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

abstract class AbstractGameRolePlayerArenaSwitchServerEventHandler<T : INetworkMessage> : EventHandler<T> {

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