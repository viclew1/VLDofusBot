package fr.lewon.dofus.bot.handlers.arena

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.fight.arena.GameRolePlayArenaSwitchToGameServerMessage
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

object GameRolePlayArenaSwitchToGameEventHandler :
    AbstractGameRolePlayerArenaSwitchServerEventHandler<GameRolePlayArenaSwitchToGameServerMessage>() {

    override fun isConnectionsValid(gameInfo: GameInfo, connections: List<DofusConnection>): Boolean {
        return GameSnifferUtil.getConnections(gameInfo.character).size == 1
    }

}