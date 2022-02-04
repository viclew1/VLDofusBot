package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.core.d2o.managers.entity.MonsterManager
import fr.lewon.dofus.bot.core.model.spell.DofusSpellLevel
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameActionFightMultipleSummonMessage
import fr.lewon.dofus.bot.sniffer.model.types.fight.summon.spawn.SpawnInformation
import fr.lewon.dofus.bot.sniffer.model.types.fight.summon.spawn.monster.BaseSpawnMonsterInformation
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameActionFightMultipleSummonEventHandler : EventHandler<GameActionFightMultipleSummonMessage> {
    override fun onEventReceived(socketResult: GameActionFightMultipleSummonMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        socketResult.summons.forEach {
            val spells = getSpells(it.spawnInformation)
            it.summons.forEach { basicSpawnInfo ->
                val fighterId = basicSpawnInfo.informations.contextualId
                val cellId = basicSpawnInfo.informations.disposition.cellId
                val teamId = basicSpawnInfo.teamId
                gameInfo.fightBoard.summonFighter(fighterId, cellId, spells, teamId)
            }
        }
    }

    private fun getSpells(spawnInformation: SpawnInformation): List<DofusSpellLevel> {
        return if (spawnInformation is BaseSpawnMonsterInformation) {
            MonsterManager.getMonster(spawnInformation.creatureGenericId.toDouble()).spells
                .mapNotNull { it.levels.lastOrNull() }
        } else emptyList()
    }
}