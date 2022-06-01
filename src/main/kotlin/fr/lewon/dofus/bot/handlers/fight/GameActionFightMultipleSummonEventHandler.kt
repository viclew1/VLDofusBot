package fr.lewon.dofus.bot.handlers.fight

import fr.lewon.dofus.bot.game.fight.DofusCharacteristics
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameActionFightMultipleSummonMessage
import fr.lewon.dofus.bot.sniffer.model.types.fight.GameContextBasicSpawnInformation
import fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.GameFightEntityInformation
import fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.GameFightFighterInformations
import fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.ai.GameFightMonsterInformations
import fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.named.GameFightFighterNamedInformations
import fr.lewon.dofus.bot.sniffer.model.types.fight.summon.GameContextSummonsInformation
import fr.lewon.dofus.bot.sniffer.model.types.fight.summon.spawn.character.SpawnCharacterInformation
import fr.lewon.dofus.bot.sniffer.model.types.fight.summon.spawn.companion.SpawnCompanionInformation
import fr.lewon.dofus.bot.sniffer.model.types.fight.summon.spawn.monster.SpawnMonsterInformation
import fr.lewon.dofus.bot.sniffer.model.types.fight.summon.spawn.monster.SpawnScaledMonsterInformation
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameActionFightMultipleSummonEventHandler : IEventHandler<GameActionFightMultipleSummonMessage> {

    override fun onEventReceived(socketResult: GameActionFightMultipleSummonMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        for (summons in socketResult.summons) {
            for (summon in summons.summons) {
                val fighterInfo = buildFighterInfo(summons, summon)
                val fighter = gameInfo.fightBoard.createOrUpdateFighter(fighterInfo)
                gameInfo.fightBoard.updateFighterCharacteristics(fighter, summons.stats.characteristics.characteristics)
                val hp = DofusCharacteristics.LIFE_POINTS.getValue(fighter) +
                        DofusCharacteristics.VITALITY.getValue(fighter)
                fighter.maxHp = hp
                fighter.baseHp = hp
            }
        }
    }

    private fun buildFighterInfo(
        summonsInfo: GameContextSummonsInformation,
        summonInfo: GameContextBasicSpawnInformation
    ): GameFightFighterInformations {
        val contextualId = summonInfo.informations.contextualId
        val disposition = summonInfo.informations.disposition
        val look = summonsInfo.look
        val wave = summonsInfo.wave
        val stats = summonsInfo.stats
        return when (val spawnInfo = summonsInfo.spawnInformation) {
            is SpawnCharacterInformation -> GameFightFighterNamedInformations().also {
                it.initGameFightFighterNamedInformations(
                    contextualId, disposition, look, summonInfo, wave, stats, ArrayList(), spawnInfo.name
                )
            }
            is SpawnCompanionInformation -> GameFightEntityInformation().also {
                val modelId = spawnInfo.modelId
                val level = spawnInfo.level
                val ownerId = spawnInfo.ownerId
                it.initGameFightEntityInformation(
                    contextualId, disposition, look, summonInfo, wave, stats, ArrayList(), modelId, level, ownerId
                )
            }
            is SpawnMonsterInformation -> GameFightMonsterInformations().also {
                val genericId = spawnInfo.creatureGenericId
                val grade = spawnInfo.creatureGrade
                it.initGameFightMonsterInformations(
                    contextualId, disposition, look, summonInfo, wave, stats, ArrayList(), genericId, grade, 0
                )
            }
            is SpawnScaledMonsterInformation -> GameFightMonsterInformations().also {
                val genericId = spawnInfo.creatureGenericId
                val level = spawnInfo.creatureLevel
                it.initGameFightMonsterInformations(
                    contextualId, disposition, look, summonInfo, wave, stats, ArrayList(), genericId, 0, level
                )
            }
            else -> GameFightFighterInformations().also {
                it.initGameFightFighterInformations(
                    contextualId, disposition, look, summonInfo, wave, stats, ArrayList()
                )
            }
        }
    }

}