package fr.lewon.dofus.bot.handlers.fight

import fr.lewon.dofus.bot.core.d2o.managers.entity.MonsterManager
import fr.lewon.dofus.bot.game.fight.DofusCharacteristics
import fr.lewon.dofus.bot.game.fight.Fighter
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameFightShowFighterMessage
import fr.lewon.dofus.bot.sniffer.model.types.fight.charac.impl.CharacterCharacteristicValue
import fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.GameFightFighterInformations
import fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.ai.GameFightMonsterInformations
import fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.named.GameFightCharacterInformations
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameFightShowFighterEventHandler : IEventHandler<GameFightShowFighterMessage> {

    override fun onEventReceived(socketResult: GameFightShowFighterMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        val fighterInfo = socketResult.informations
        val fighterId = fighterInfo.contextualId
        val fighter = gameInfo.fightBoard.createOrUpdateFighter(fighterInfo)
        val characteristics = fighterInfo.stats.characteristics.characteristics
        gameInfo.fightBoard.updateFighterCharacteristics(fighter, characteristics)
        if (fighterId == gameInfo.playerId) {
            gameInfo.updatePlayerFighter()
        } else {
            updateFighter(fighter, fighterInfo)
        }
    }

    private fun updateFighter(fighter: Fighter, fighterInfo: GameFightFighterInformations) {
        val hp = DofusCharacteristics.LIFE_POINTS.getValue(fighter)
        fighter.maxHp = hp
        fighter.baseHp = hp
        if (fighterInfo is GameFightMonsterInformations) {
            updateMonsterFighter(fighter, fighterInfo.creatureGenericId.toDouble())
        } else if (fighterInfo is GameFightCharacterInformations) {
            updateCharacterFighter(fighter)
        }
    }

    private fun updateMonsterFighter(fighter: Fighter, monsterGenericId: Double) {
        val baseStats = MonsterManager.getMonster(monsterGenericId).baseStats
        fighter.baseStatsById.putAll(baseStats.entries.associate {
            it.key.id to CharacterCharacteristicValue().also { ccv -> ccv.total = it.value }
        })
    }

    private fun updateCharacterFighter(fighter: Fighter) {
        val vitality = DofusCharacteristics.VITALITY.getValue(fighter)
        val curLife = DofusCharacteristics.CUR_LIFE.getValue(fighter)
        fighter.maxHp += vitality
        fighter.baseHp += vitality
        fighter.hpLost -= curLife
    }

}