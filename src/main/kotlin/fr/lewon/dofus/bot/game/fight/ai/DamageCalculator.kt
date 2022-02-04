package fr.lewon.dofus.bot.game.fight.ai

import fr.lewon.dofus.bot.core.model.spell.DofusSpellEffect
import fr.lewon.dofus.bot.core.model.spell.DofusSpellEffectType
import fr.lewon.dofus.bot.core.model.spell.DofusSpellLevel
import fr.lewon.dofus.bot.core.model.spell.DofusSpellTarget
import fr.lewon.dofus.bot.game.fight.DofusCharacteristics
import fr.lewon.dofus.bot.game.fight.Fighter

class DamageCalculator {

    private val cachedDamagesByFighter = HashMap<Double, HashMap<Pair<Double, DofusSpellLevel>, Int>>()

    fun getRealDamage(spellLevel: DofusSpellLevel, caster: Fighter, target: Fighter): Int {
        val cachedDamages = cachedDamagesByFighter.computeIfAbsent(caster.id) { HashMap() }
        return cachedDamages.computeIfAbsent(target.id to spellLevel) {
            computeRealDamage(spellLevel, caster, target)
        }
    }

    private fun computeRealDamage(spellLevel: DofusSpellLevel, caster: Fighter, target: Fighter): Int {
        val criticalChance = spellLevel.criticalHitProbability + DofusCharacteristics.CRITICAL_HIT.getValue(caster)
        return if (criticalChance < 60) {
            computeDamage(spellLevel.effects, caster, target, false)
        } else {
            computeDamage(spellLevel.criticalEffects, caster, target, true)
        }
    }

    private fun computeDamage(
        spellEffects: List<DofusSpellEffect>,
        caster: Fighter,
        target: Fighter,
        criticalHit: Boolean
    ): Int {
        return spellEffects.sumOf { computeDamage(it, caster, target, criticalHit) }
    }

    private fun computeDamage(
        spellEffect: DofusSpellEffect,
        caster: Fighter,
        target: Fighter,
        criticalHit: Boolean
    ): Int {
        if (!effectCanHitTarget(spellEffect.target, caster, target)) {
            return 0
        }
        val elementCharac: DofusCharacteristics
        val elementDamages: DofusCharacteristics
        val elementResistPer: DofusCharacteristics
        val elementResist: DofusCharacteristics
        when (spellEffect.effectType) {
            DofusSpellEffectType.AIR_DAMAGE, DofusSpellEffectType.AIR_LIFE_STEAL -> {
                elementCharac = DofusCharacteristics.AGILITY
                elementDamages = DofusCharacteristics.AIR_DAMAGE_BONUS
                elementResistPer = DofusCharacteristics.AIR_ELEMENT_RESIST_PERCENT
                elementResist = DofusCharacteristics.AIR_ELEMENT_REDUCTION
            }
            DofusSpellEffectType.WATER_DAMAGE, DofusSpellEffectType.WATER_LIFE_STEAL -> {
                elementCharac = DofusCharacteristics.CHANCE
                elementDamages = DofusCharacteristics.WATER_DAMAGE_BONUS
                elementResistPer = DofusCharacteristics.WATER_ELEMENT_RESIST_PERCENT
                elementResist = DofusCharacteristics.WATER_ELEMENT_REDUCTION
            }
            DofusSpellEffectType.EARTH_DAMAGE, DofusSpellEffectType.EARTH_LIFE_STEAL -> {
                elementCharac = DofusCharacteristics.STRENGTH
                elementDamages = DofusCharacteristics.EARTH_DAMAGE_BONUS
                elementResistPer = DofusCharacteristics.EARTH_ELEMENT_RESIST_PERCENT
                elementResist = DofusCharacteristics.EARTH_ELEMENT_REDUCTION
            }
            DofusSpellEffectType.FIRE_DAMAGE, DofusSpellEffectType.FIRE_LIFE_STEAL -> {
                elementCharac = DofusCharacteristics.INTELLIGENCE
                elementDamages = DofusCharacteristics.FIRE_DAMAGE_BONUS
                elementResistPer = DofusCharacteristics.FIRE_ELEMENT_RESIST_PERCENT
                elementResist = DofusCharacteristics.FIRE_ELEMENT_REDUCTION
            }
            DofusSpellEffectType.NEUTRAL_DAMAGE, DofusSpellEffectType.NEUTRAL_LIFE_STEAL -> {
                elementCharac = DofusCharacteristics.STRENGTH
                elementDamages = DofusCharacteristics.NEUTRAL_DAMAGE_BONUS
                elementResistPer = DofusCharacteristics.NEUTRAL_ELEMENT_RESIST_PERCENT
                elementResist = DofusCharacteristics.NEUTRAL_ELEMENT_REDUCTION
            }
            else -> return 0
        }
        return computeDamage(
            spellEffect.min, caster, target, elementCharac, elementDamages, elementResistPer, elementResist, criticalHit
        )
    }

    private fun effectCanHitTarget(spellTarget: DofusSpellTarget, caster: Fighter, target: Fighter): Boolean {
        return when (spellTarget) {
            DofusSpellTarget.ENEMIES_ONLY -> caster.teamId != target.teamId
            DofusSpellTarget.ALLIES_ONLY -> caster.teamId == target.teamId
            DofusSpellTarget.EVERYBODY -> true
        }
    }

    private fun computeDamage(
        baseDamage: Int,
        caster: Fighter,
        target: Fighter,
        elementCharac: DofusCharacteristics,
        elementDamages: DofusCharacteristics,
        elementResistPercent: DofusCharacteristics,
        elementResist: DofusCharacteristics,
        criticalHit: Boolean
    ): Int {
        val characValue = elementCharac.getValue(caster) + DofusCharacteristics.DAMAGES_BONUS_PERCENT.getValue(caster)
        var damagesValue = elementDamages.getValue(caster) + DofusCharacteristics.ALL_DAMAGES_BONUS.getValue(caster)
        if (criticalHit) {
            damagesValue += DofusCharacteristics.CRITICAL_DAMAGE_BONUS.getValue(caster)
        }
        val damage = (baseDamage.toFloat() * (100f + characValue.toFloat()) / 100f).toInt() + damagesValue
        var damageMultiplier = if (caster.cell.neighbors.map { it.cellId }.contains(target.cell.cellId)) {
            DofusCharacteristics.MELEE_DAMAGE_DONE_PERCENT.getValue(caster)
        } else {
            DofusCharacteristics.RANGED_DAMAGE_DONE_PERCENT.getValue(caster)
        }
        damageMultiplier += DofusCharacteristics.SPELL_DAMAGE_DONE_PERCENT.getValue(caster) - 100
        val multipliedDamages = (damage.toFloat() * (damageMultiplier.toFloat() / 100f)).toInt()
        val enemyResistPercent = elementResistPercent.getValue(target)
        val enemyResist = elementResist.getValue(target)
        val resistProduct = enemyResistPercent.toFloat() / 100f
        return (multipliedDamages * (1f - resistProduct) - enemyResist).toInt()
    }

}