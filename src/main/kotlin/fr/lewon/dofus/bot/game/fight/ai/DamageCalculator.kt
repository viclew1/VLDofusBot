package fr.lewon.dofus.bot.game.fight.ai

import fr.lewon.dofus.bot.core.model.spell.DofusSpellEffect
import fr.lewon.dofus.bot.core.model.spell.DofusSpellEffectType
import fr.lewon.dofus.bot.core.model.spell.DofusSpellLevel
import fr.lewon.dofus.bot.core.model.spell.DofusSpellTarget
import fr.lewon.dofus.bot.game.fight.DofusCharacteristics
import fr.lewon.dofus.bot.game.fight.Fighter

class DamageCalculator {

    private val cachedDamages = HashMap<Pair<Fighter, DofusSpellLevel>, Int>()

    fun resetCache() {
        cachedDamages.clear()
    }

    fun getRealDamage(spellLevel: DofusSpellLevel, player: Fighter, target: Fighter): Int {
        return cachedDamages.computeIfAbsent(target to spellLevel) {
            computeRealDamage(spellLevel, player, target)
        }
    }

    private fun computeRealDamage(spellLevel: DofusSpellLevel, player: Fighter, target: Fighter): Int {
        val criticalChance = spellLevel.criticalHitProbability + DofusCharacteristics.CRITICAL_HIT.getValue(player)
        return if (criticalChance < 60) {
            computeDamage(spellLevel.effects, player, target, false)
        } else {
            computeDamage(spellLevel.criticalEffects, player, target, true)
        }
    }

    private fun computeDamage(
        spellEffects: List<DofusSpellEffect>,
        player: Fighter,
        target: Fighter,
        criticalHit: Boolean
    ): Int {
        return spellEffects.sumOf { computeDamage(it, player, target, criticalHit) }
    }

    private fun computeDamage(
        spellEffect: DofusSpellEffect,
        player: Fighter,
        target: Fighter,
        criticalHit: Boolean
    ): Int {
        if (!effectCanHitTarget(spellEffect.target, player, target)) {
            return 0
        }
        val elementCharac: DofusCharacteristics
        val elementDamages: DofusCharacteristics
        val elementResistPercent: DofusCharacteristics
        val elementResist: DofusCharacteristics
        when (spellEffect.effectType) {
            DofusSpellEffectType.AIR_DAMAGE, DofusSpellEffectType.AIR_LIFE_STEAL -> {
                elementCharac = DofusCharacteristics.AGILITY
                elementDamages = DofusCharacteristics.AIR_DAMAGE_BONUS
                elementResistPercent = DofusCharacteristics.AIR_ELEMENT_RESIST_PERCENT
                elementResist = DofusCharacteristics.AIR_ELEMENT_REDUCTION
            }
            DofusSpellEffectType.WATER_DAMAGE, DofusSpellEffectType.WATER_LIFE_STEAL -> {
                elementCharac = DofusCharacteristics.CHANCE
                elementDamages = DofusCharacteristics.WATER_DAMAGE_BONUS
                elementResistPercent = DofusCharacteristics.WATER_ELEMENT_RESIST_PERCENT
                elementResist = DofusCharacteristics.WATER_ELEMENT_REDUCTION
            }
            DofusSpellEffectType.EARTH_DAMAGE, DofusSpellEffectType.EARTH_LIFE_STEAL -> {
                elementCharac = DofusCharacteristics.STRENGTH
                elementDamages = DofusCharacteristics.EARTH_DAMAGE_BONUS
                elementResistPercent = DofusCharacteristics.EARTH_ELEMENT_RESIST_PERCENT
                elementResist = DofusCharacteristics.EARTH_ELEMENT_REDUCTION
            }
            DofusSpellEffectType.FIRE_DAMAGE, DofusSpellEffectType.FIRE_LIFE_STEAL -> {
                elementCharac = DofusCharacteristics.INTELLIGENCE
                elementDamages = DofusCharacteristics.FIRE_DAMAGE_BONUS
                elementResistPercent = DofusCharacteristics.FIRE_ELEMENT_RESIST_PERCENT
                elementResist = DofusCharacteristics.FIRE_ELEMENT_REDUCTION
            }
            DofusSpellEffectType.NEUTRAL_DAMAGE, DofusSpellEffectType.NEUTRAL_LIFE_STEAL -> {
                elementCharac = DofusCharacteristics.STRENGTH
                elementDamages = DofusCharacteristics.NEUTRAL_DAMAGE_BONUS
                elementResistPercent = DofusCharacteristics.NEUTRAL_ELEMENT_RESIST_PERCENT
                elementResist = DofusCharacteristics.NEUTRAL_ELEMENT_REDUCTION
            }
            else -> return 0
        }
        return computeDamage(
            spellEffect.min,
            player,
            target,
            elementCharac,
            elementDamages,
            elementResistPercent,
            elementResist,
            criticalHit
        )
    }

    private fun effectCanHitTarget(spellTarget: DofusSpellTarget, player: Fighter, target: Fighter): Boolean {
        return when (spellTarget) {
            DofusSpellTarget.ENEMIES_ONLY -> player.teamId != target.teamId
            DofusSpellTarget.ALLIES_ONLY -> player.teamId == target.teamId
            DofusSpellTarget.EVERYBODY -> true
        }
    }

    private fun computeDamage(
        baseDamage: Int,
        fighter: Fighter,
        target: Fighter,
        elementCharac: DofusCharacteristics,
        elementDamages: DofusCharacteristics,
        elementResistPercent: DofusCharacteristics,
        elementResist: DofusCharacteristics,
        criticalHit: Boolean
    ): Int {
        val characValue = elementCharac.getValue(fighter) + DofusCharacteristics.DAMAGES_BONUS_PERCENT.getValue(fighter)
        var damagesValue = elementDamages.getValue(fighter) + DofusCharacteristics.ALL_DAMAGES_BONUS.getValue(fighter)
        if (criticalHit) {
            damagesValue += DofusCharacteristics.CRITICAL_DAMAGE_BONUS.getValue(fighter)
        }
        val damage = (baseDamage.toFloat() * (100f + characValue.toFloat()) / 100f).toInt() + damagesValue
        var damageMultiplier = if (fighter.cell.neighbors.map { it.cellId }.contains(target.cell.cellId)) {
            DofusCharacteristics.MELEE_DAMAGE_DONE_PERCENT.getValue(fighter)
        } else {
            DofusCharacteristics.RANGED_DAMAGE_DONE_PERCENT.getValue(fighter)
        }
        damageMultiplier += DofusCharacteristics.SPELL_DAMAGE_DONE_PERCENT.getValue(fighter) - 100
        val multipliedDamages = (damage.toFloat() * (damageMultiplier.toFloat() / 100f)).toInt()
        val enemyResistPercent = elementResistPercent.getValue(target)
        val enemyResist = elementResist.getValue(target)
        val resistProduct = enemyResistPercent.toFloat() / 100f
        return (multipliedDamages * (1f - resistProduct) - enemyResist).toInt().also { println("Result : $it") }
    }

}