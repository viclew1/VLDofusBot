package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.arena.GameRolePlayArenaSwitchToGameServerMessage
import fr.lewon.dofus.bot.util.network.GameInfo
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameRolePlayArenaSwitchToGameEventHandler :
    AbstractGameRolePlayerArenaSwitchServerEventHandler<GameRolePlayArenaSwitchToGameServerMessage>() {

    override fun isConnectionsValid(gameInfo: GameInfo, connections: List<DofusConnection>): Boolean {
        return GameSnifferUtil.getConnections(gameInfo.character).size == 1
    }

}