package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.core.d2o.managers.entity.MonsterManager
import fr.lewon.dofus.bot.core.model.spell.DofusSpell
import fr.lewon.dofus.bot.core.model.spell.DofusSpellLevel
import fr.lewon.dofus.bot.game.fight.DofusCharacteristics
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameFightShowFighterMessage
import fr.lewon.dofus.bot.sniffer.model.types.fight.charac.impl.CharacterCharacteristicValue
import fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.GameFightFighterInformations
import fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.ai.GameFightMonsterInformations
import fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.named.GameFightCharacterInformations
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameInfo
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameFightShowFighterEventHandler : EventHandler<GameFightShowFighterMessage> {

    override fun onEventReceived(socketResult: GameFightShowFighterMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        val fighterInfo = socketResult.informations
        val fighterId = fighterInfo.contextualId
        val cellId = fighterInfo.spawnInfo.informations.disposition.cellId
        val characteristics = fighterInfo.stats.characteristics.characteristics
        val teamId = fighterInfo.spawnInfo.teamId
        val spells = getSpellLevels(gameInfo, fighterInfo, fighterId)
        gameInfo.fightBoard.createOrUpdateFighter(fighterId, cellId, spells, teamId)
        gameInfo.fightBoard.updateFighterCharacteristics(fighterId, characteristics)
        if (fighterId == gameInfo.playerId) {
            gameInfo.updatePlayerFighter()
        } else {
            val fighter = gameInfo.fightBoard.getFighterById(fighterId)
                ?: error("Fighter $fighterId should exist")
            val hp = DofusCharacteristics.LIFE_POINTS.getValue(fighter)
            fighter.maxHp = hp
            fighter.baseHp = hp
            if (fighterInfo is GameFightMonsterInformations) {
                val baseStats = MonsterManager.getMonster(fighterInfo.creatureGenericId.toDouble()).baseStats
                fighter.baseStatsById.putAll(baseStats.entries.associate {
                    it.key.id to CharacterCharacteristicValue().also { ccv -> ccv.total = it.value }
                })
            }
        }
    }

    private fun getSpellLevels(
        gameInfo: GameInfo,
        fighterInfo: GameFightFighterInformations,
        fighterId: Double
    ): List<DofusSpellLevel> {
        return when {
            fighterInfo is GameFightMonsterInformations -> {
                val spells = MonsterManager.getMonster(fighterInfo.creatureGenericId.toDouble()).spells
                getSpellLevels(spells, fighterInfo.creatureLevel)
            }
            fighterInfo is GameFightCharacterInformations && fighterId == gameInfo.playerId -> {
                val spells = gameInfo.character.characterSpells.map { it.spell }
                getSpellLevels(spells, fighterInfo.level)
            }
            else -> emptyList()
        }
    }

    private fun getLevel(gameInfo: GameInfo, fighterInfo: GameFightFighterInformations, fighterId: Double): Int {
        return when {
            fighterInfo is GameFightMonsterInformations -> fighterInfo.creatureLevel
            fighterInfo is GameFightCharacterInformations && fighterId == gameInfo.playerId -> fighterInfo.level
            else -> 0
        }
    }

    private fun getSpellLevels(spells: List<DofusSpell>, level: Int): List<DofusSpellLevel> {
        return spells.mapNotNull { getSpellLevel(it, level) }
    }

    private fun getSpellLevel(spell: DofusSpell, level: Int): DofusSpellLevel? {
        return spell.levels.filter { it.minPlayerLevel <= level }.maxByOrNull { it.minPlayerLevel }
    }

}