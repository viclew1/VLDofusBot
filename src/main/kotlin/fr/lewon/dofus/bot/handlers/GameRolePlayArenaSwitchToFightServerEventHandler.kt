package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.arena.GameRolePlayArenaSwitchToFightServerMessage
import fr.lewon.dofus.bot.util.network.GameInfo
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameRolePlayArenaSwitchToFightServerEventHandler :
    AbstractGameRolePlayerArenaSwitchServerEventHandler<GameRolePlayArenaSwitchToFightServerMessage>() {

    override fun isConnectionsValid(gameInfo: GameInfo, connections: List<DofusConnection>): Boolean {
        return GameSnifferUtil.getConnections(gameInfo.character).size > 1
    }

}