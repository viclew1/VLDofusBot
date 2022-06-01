package fr.lewon.dofus.bot.handlers.arena

import fr.lewon.dofus.bot.core.d2o.managers.entity.MonsterManager
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.misc.GameRolePlayShowActorMessage
import fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.monster.GameRolePlayGroupMonsterInformations
import fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.npc.GameRolePlayNpcInformations
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameRolePlayShowActorEventHandler : IEventHandler<GameRolePlayShowActorMessage> {
    override fun onEventReceived(socketResult: GameRolePlayShowActorMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        val entityInfo = socketResult.information
        if (entityInfo is GameRolePlayNpcInformations) {
            gameInfo.entityIdByNpcId[entityInfo.npcId] = entityInfo.contextualId
        } else if (entityInfo is GameRolePlayGroupMonsterInformations) {
            gameInfo.monsterInfoByEntityId[entityInfo.contextualId] = entityInfo
            gameInfo.mainMonstersByGroupOnMap[entityInfo] = MonsterManager.getMonster(
                entityInfo.staticInfos.mainCreatureLightInfos.genericId.toDouble()
            )
        }
        gameInfo.entityPositionsOnMapByEntityId[entityInfo.contextualId] = entityInfo.disposition.cellId
    }
}