package fr.lewon.dofus.bot.game.fight

import fr.lewon.dofus.bot.core.d2o.managers.entity.MonsterManager
import fr.lewon.dofus.bot.core.fighter.IDofusFighter
import fr.lewon.dofus.bot.core.fighter.PlayerType
import fr.lewon.dofus.bot.core.model.entity.DofusMonster
import fr.lewon.dofus.bot.core.model.spell.DofusSpellLevel
import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.sniffer.model.types.fight.charac.CharacterCharacteristic
import fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.GameFightEntityInformation
import fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.GameFightFighterInformations
import fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.ai.GameFightAIInformations
import fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.ai.GameFightMonsterInformations
import fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.named.GameFightCharacterInformations
import fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.named.GameFightFighterNamedInformations
import kotlin.math.min

class Fighter(
    var cell: DofusCell,
    var id: Double,
    private val fighterInfo: GameFightFighterInformations,
    var spells: List<DofusSpellLevel> = ArrayList(),
    val baseStatsById: MutableMap<Int, CharacterCharacteristic> = HashMap(),
    val statsById: MutableMap<Int, CharacterCharacteristic> = HashMap(),
    val states: ArrayList<Int> = ArrayList(),
    private var monsterProperties: DofusMonster? = if (fighterInfo is GameFightMonsterInformations) {
        MonsterManager.getMonster(fighterInfo.creatureGenericId.toDouble())
    } else null
) : IDofusFighter {

    var teamId = fighterInfo.spawnInfo.teamId
    var invisibilityState = fighterInfo.stats.invisibilityState

    var maxHp = 0
    var hpLost = 0
    var hpHealed = 0
    var baseHp = 0
    var shield = 0
    var totalMp = 0

    override fun getFighterTeamId(): Int {
        return teamId
    }

    override fun getFighterId(): Double {
        return id
    }

    override fun getPlayerType(): PlayerType {
        if (this.fighterInfo is GameFightFighterNamedInformations) {
            return PlayerType.HUMAN
        }
        if (this.fighterInfo is GameFightEntityInformation) {
            return PlayerType.SIDEKICK
        }
        if (this.fighterInfo is GameFightAIInformations) {
            return PlayerType.MONSTER
        }
        return PlayerType.UNKNOWN
    }

    override fun getBreed(): Int {
        if (this.fighterInfo is GameFightCharacterInformations) {
            return fighterInfo.breed
        }
        if (this.fighterInfo is GameFightMonsterInformations) {
            return fighterInfo.creatureGenericId
        }
        return -1
    }

    fun isVisible(): Boolean {
        return invisibilityState != 1
    }

    override fun isSummon(): Boolean {
        return fighterInfo.stats.summoned
    }

    override fun isStaticElement(): Boolean {
        return false
    }

    override fun hasState(state: Int): Boolean {
        return states.contains(state)
    }

    fun useSummonSlot(): Boolean {
        return monsterProperties?.useSummonSlot ?: false
    }

    override fun getSummonerId(): Double {
        return fighterInfo.stats.summoner
    }

    fun canBreedSwitchPos(): Boolean {
        return monsterProperties?.canSwitchPos != true
    }

    fun canBreedSwitchPosOnTarget(): Boolean {
        return monsterProperties?.canSwitchPosOnTarget != true
    }

    fun canBreedBePushed(): Boolean {
        return monsterProperties?.canBePushed != true
    }

    fun deepCopy(): Fighter {
        return Fighter(
            cell, id, fighterInfo, spells, HashMap(baseStatsById),
            HashMap(statsById), ArrayList(states), monsterProperties
        ).also {
            it.maxHp = maxHp
            it.hpLost = hpLost
            it.hpHealed = hpHealed
            it.baseHp = baseHp
            it.teamId = teamId
            it.totalMp = totalMp
            it.invisibilityState = invisibilityState
        }
    }

    fun getCurrentHp(): Int {
        return min(maxHp + shield, baseHp + shield - hpLost + hpHealed)
    }
}