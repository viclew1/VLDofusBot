package fr.lewon.dofus.bot.handlers.fight

import fr.lewon.dofus.bot.game.fight.DofusCharacteristics
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.actions.fight.GameActionFightMultipleSummonMessage
import fr.lewon.dofus.bot.sniffer.model.types.game.context.EntityDispositionInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.context.GameContextActorInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.context.GameContextActorPositionInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.*
import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
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
                initGameFightFighterNamedInformations(
                    it, contextualId, disposition, look, summonInfo, wave, stats, ArrayList(), spawnInfo.name
                )
            }
            is SpawnCompanionInformation -> GameFightEntityInformation().also {
                val modelId = spawnInfo.modelId
                val level = spawnInfo.level
                val ownerId = spawnInfo.ownerId
                initGameFightEntityInformation(
                    it, contextualId, disposition, look, summonInfo, wave, stats, ArrayList(), modelId, level, ownerId
                )
            }
            is SpawnMonsterInformation -> GameFightMonsterInformations().also {
                val genericId = spawnInfo.creatureGenericId
                val grade = spawnInfo.creatureGrade
                initGameFightMonsterInformations(
                    it, contextualId, disposition, look, summonInfo, wave, stats, ArrayList(), genericId, grade, 0
                )
            }
            is SpawnScaledMonsterInformation -> GameFightMonsterInformations().also {
                val genericId = spawnInfo.creatureGenericId
                val level = spawnInfo.creatureLevel
                initGameFightMonsterInformations(
                    it, contextualId, disposition, look, summonInfo, wave, stats, ArrayList(), genericId, 0, level
                )
            }
            else -> GameFightFighterInformations().also {
                initGameFightFighterInformations(
                    it, contextualId, disposition, look, summonInfo, wave, stats, ArrayList()
                )
            }
        }
    }

    private fun initGameFightFighterNamedInformations(
        fighterInformation: GameFightFighterNamedInformations,
        contextualId: Double,
        disposition: EntityDispositionInformations,
        look: EntityLook,
        spawnInfo: GameContextBasicSpawnInformation,
        wave: Int,
        stats: GameFightCharacteristics,
        previousPositions: ArrayList<Int>,
        name: String
    ) {
        initGameFightFighterInformations(
            fighterInformation, contextualId, disposition, look, spawnInfo, wave, stats, previousPositions
        )
        fighterInformation.name = name
    }

    private fun initGameFightEntityInformation(
        fighterInformation: GameFightEntityInformation,
        contextualId: Double,
        disposition: EntityDispositionInformations,
        look: EntityLook,
        spawnInfo: GameContextBasicSpawnInformation,
        wave: Int,
        stats: GameFightCharacteristics,
        previousPositions: ArrayList<Int>,
        entityModelId: Int,
        level: Int,
        masterId: Double
    ) {
        initGameFightFighterInformations(
            fighterInformation, contextualId, disposition, look, spawnInfo, wave, stats, previousPositions
        )
        fighterInformation.entityModelId = entityModelId
        fighterInformation.level = level
        fighterInformation.masterId = masterId
    }

    private fun initGameFightMonsterInformations(
        fighterInformation: GameFightMonsterInformations,
        contextualId: Double,
        disposition: EntityDispositionInformations,
        look: EntityLook,
        spawnInfo: GameContextBasicSpawnInformation,
        wave: Int,
        stats: GameFightCharacteristics,
        previousPositions: ArrayList<Int>,
        creatureGenericId: Int,
        creatureGrade: Int,
        creatureLevel: Int
    ) {
        initGameFightAIInformations(
            fighterInformation, contextualId, disposition, look, spawnInfo, wave, stats, previousPositions
        )
        fighterInformation.creatureGenericId = creatureGenericId
        fighterInformation.creatureGrade = creatureGrade
        fighterInformation.creatureLevel = creatureLevel
    }

    private fun initGameFightAIInformations(
        fighterInformation: GameFightAIInformations,
        contextualId: Double,
        disposition: EntityDispositionInformations,
        look: EntityLook,
        spawnInfo: GameContextBasicSpawnInformation,
        wave: Int,
        stats: GameFightCharacteristics,
        previousPositions: ArrayList<Int>
    ) {
        initGameFightFighterInformations(
            fighterInformation, contextualId, disposition, look, spawnInfo, wave, stats, previousPositions
        )
    }

    private fun initGameFightFighterInformations(
        fighterInformation: GameFightFighterInformations,
        contextualId: Double,
        disposition: EntityDispositionInformations,
        look: EntityLook,
        spawnInfo: GameContextBasicSpawnInformation,
        wave: Int,
        stats: GameFightCharacteristics,
        previousPositions: ArrayList<Int>
    ) {
        initGameContextActorInformations(fighterInformation, contextualId, disposition, look)
        fighterInformation.spawnInfo = spawnInfo
        fighterInformation.wave = wave
        fighterInformation.stats = stats
        fighterInformation.previousPositions = previousPositions
    }

    private fun initGameContextActorInformations(
        fighterInformation: GameContextActorInformations,
        contextualId: Double,
        disposition: EntityDispositionInformations,
        look: EntityLook
    ) {
        initGameContextActorPositionInformations(fighterInformation, contextualId, disposition)
        fighterInformation.look = look
    }

    private fun initGameContextActorPositionInformations(
        fighterInformation: GameContextActorPositionInformations,
        contextualId: Double,
        disposition: EntityDispositionInformations
    ) {
        fighterInformation.contextualId = contextualId
        fighterInformation.disposition = disposition
    }

}