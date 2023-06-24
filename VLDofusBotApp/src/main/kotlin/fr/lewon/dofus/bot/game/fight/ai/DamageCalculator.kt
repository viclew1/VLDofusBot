package fr.lewon.dofus.bot.game.fight.ai

import fr.lewon.dofus.bot.core.model.spell.DofusSpellEffect
import fr.lewon.dofus.bot.core.model.spell.DofusSpellEffectType
import fr.lewon.dofus.bot.core.model.spell.DofusSpellTarget
import fr.lewon.dofus.bot.game.fight.DofusCharacteristics
import fr.lewon.dofus.bot.game.fight.Fighter

class DamageCalculator {

    private val cachedEffectDamagesByFighter = HashMap<Double, HashMap<EffectKey, Int>>()

    private data class EffectKey(val targetId: Double, val spellEffect: DofusSpellEffect, val criticalHit: Boolean)

    fun getRealEffectDamage(
        spellEffect: DofusSpellEffect,
        caster: Fighter,
        target: Fighter,
        criticalHit: Boolean,
        upperBound: Boolean
    ): Int {
        val cachedDamages = cachedEffectDamagesByFighter.computeIfAbsent(caster.id) { HashMap() }
        return cachedDamages.computeIfAbsent(EffectKey(target.id, spellEffect, criticalHit)) {
            computeEffectDamage(spellEffect, caster, target, criticalHit, upperBound)
        }
    }

    private fun computeEffectDamage(
        spellEffect: DofusSpellEffect,
        caster: Fighter,
        target: Fighter,
        criticalHit: Boolean,
        upperBound: Boolean
    ): Int {
        if (!effectCanHitTarget(spellEffect.targets, caster, target)) {
            return 0
        }
        val baseDamage = if (upperBound) spellEffect.max else spellEffect.min
        val elementalDamageInfo = getElementalDamageInfo(spellEffect.effectType)
            ?: return 0
        return computeDamage(baseDamage, caster, target, elementalDamageInfo, criticalHit)
    }

    private fun effectCanHitTarget(spellTargets: List<DofusSpellTarget>, caster: Fighter, target: Fighter): Boolean {
        return spellTargets.any {
            it.canHitTarget(caster, target)
        }
    }

    private fun computeDamage(
        baseDamage: Int,
        caster: Fighter,
        target: Fighter,
        elementalDamageInfo: ElementalDamageInfo,
        criticalHit: Boolean
    ): Int {
        val elementCharac = elementalDamageInfo.elementCharac
        val elementDamages = elementalDamageInfo.elementDamages
        val elementResistPercent = elementalDamageInfo.elementResistPercent
        val elementResist = elementalDamageInfo.elementResist
        val characValue = elementCharac.getValue(caster) + DofusCharacteristics.DAMAGES_BONUS_PERCENT.getValue(caster)
        var damagesValue = elementDamages.getValue(caster) + DofusCharacteristics.ALL_DAMAGES_BONUS.getValue(caster)
        if (criticalHit) {
            damagesValue += DofusCharacteristics.CRITICAL_DAMAGE_BONUS.getValue(caster)
        }
        val damage = (baseDamage.toFloat() * (100f + characValue.toFloat()) / 100f).toInt() + damagesValue
        var damageMultiplier = if (caster.cell.neighbors.map { it.cellId }.contains(target.cell.cellId)) {
            DofusCharacteristics.MELEE_DAMAGE_DONE_PERCENT.getValue(caster, 100)
        } else {
            DofusCharacteristics.RANGED_DAMAGE_DONE_PERCENT.getValue(caster, 100)
        }
        damageMultiplier += DofusCharacteristics.SPELL_DAMAGE_DONE_PERCENT.getValue(caster, 100) - 100
        val multipliedDamages = (damage.toFloat() * (damageMultiplier.toFloat() / 100f)).toInt()
        val enemyResistPercent = elementResistPercent.getValue(target)
        val enemyResist = elementResist.getValue(target)
        val resistProduct = enemyResistPercent.toFloat() / 100f
        return (multipliedDamages * (1f - resistProduct) - enemyResist).toInt()
    }

    private class ElementalDamageInfo(
        val elementCharac: DofusCharacteristics,
        val elementDamages: DofusCharacteristics,
        val elementResistPercent: DofusCharacteristics,
        val elementResist: DofusCharacteristics
    )

    private fun getElementalDamageInfo(effectType: DofusSpellEffectType): ElementalDamageInfo? =
        when (effectType) {
            DofusSpellEffectType.AIR_DAMAGE, DofusSpellEffectType.AIR_LIFE_STEAL -> ElementalDamageInfo(
                elementCharac = DofusCharacteristics.AGILITY,
                elementDamages = DofusCharacteristics.AIR_DAMAGE_BONUS,
                elementResistPercent = DofusCharacteristics.AIR_ELEMENT_RESIST_PERCENT,
                elementResist = DofusCharacteristics.AIR_ELEMENT_REDUCTION
            )
            DofusSpellEffectType.WATER_DAMAGE, DofusSpellEffectType.WATER_LIFE_STEAL -> ElementalDamageInfo(
                elementCharac = DofusCharacteristics.CHANCE,
                elementDamages = DofusCharacteristics.WATER_DAMAGE_BONUS,
                elementResistPercent = DofusCharacteristics.WATER_ELEMENT_RESIST_PERCENT,
                elementResist = DofusCharacteristics.WATER_ELEMENT_REDUCTION
            )
            DofusSpellEffectType.EARTH_DAMAGE, DofusSpellEffectType.EARTH_LIFE_STEAL, DofusSpellEffectType.MP_DECREASED_EARTH_DAMAGE -> ElementalDamageInfo(
                elementCharac = DofusCharacteristics.STRENGTH,
                elementDamages = DofusCharacteristics.EARTH_DAMAGE_BONUS,
                elementResistPercent = DofusCharacteristics.EARTH_ELEMENT_RESIST_PERCENT,
                elementResist = DofusCharacteristics.EARTH_ELEMENT_REDUCTION
            )
            DofusSpellEffectType.FIRE_DAMAGE, DofusSpellEffectType.FIRE_LIFE_STEAL -> ElementalDamageInfo(
                elementCharac = DofusCharacteristics.INTELLIGENCE,
                elementDamages = DofusCharacteristics.FIRE_DAMAGE_BONUS,
                elementResistPercent = DofusCharacteristics.FIRE_ELEMENT_RESIST_PERCENT,
                elementResist = DofusCharacteristics.FIRE_ELEMENT_REDUCTION
            )
            DofusSpellEffectType.NEUTRAL_DAMAGE, DofusSpellEffectType.NEUTRAL_LIFE_STEAL -> ElementalDamageInfo(
                elementCharac = DofusCharacteristics.STRENGTH,
                elementDamages = DofusCharacteristics.NEUTRAL_DAMAGE_BONUS,
                elementResistPercent = DofusCharacteristics.NEUTRAL_ELEMENT_RESIST_PERCENT,
                elementResist = DofusCharacteristics.NEUTRAL_ELEMENT_REDUCTION
            )
            else -> null
        }

}