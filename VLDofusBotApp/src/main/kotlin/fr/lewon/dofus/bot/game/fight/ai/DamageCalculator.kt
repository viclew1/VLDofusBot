package fr.lewon.dofus.bot.game.fight.ai

import fr.lewon.dofus.bot.core.model.spell.DofusSpellEffect
import fr.lewon.dofus.bot.core.model.spell.DofusSpellEffectType
import fr.lewon.dofus.bot.core.model.spell.DofusSpellTarget
import fr.lewon.dofus.bot.game.fight.DofusCharacteristics
import fr.lewon.dofus.bot.game.fight.Fighter

class DamageCalculator {

    data class DamageRange(
        val minDamage: Int,
        val maxDamage: Int,
    )

    private val cachedEffectDamagesByFighter = HashMap<Double, HashMap<EffectKey, DamageRange>>()

    private data class EffectKey(val targetId: Double, val spellEffect: DofusSpellEffect)

    fun getRealEffectDamage(
        spellEffect: DofusSpellEffect,
        caster: Fighter,
        target: Fighter,
        criticalHit: Boolean
    ): DamageRange {
        val cachedDamages = cachedEffectDamagesByFighter.computeIfAbsent(caster.id) { HashMap() }
        return cachedDamages.computeIfAbsent(EffectKey(target.id, spellEffect)) {
            computeEffectDamage(spellEffect, caster, target, criticalHit)
        }
    }

    private fun computeEffectDamage(
        spellEffect: DofusSpellEffect,
        caster: Fighter,
        target: Fighter,
        criticalHit: Boolean
    ): DamageRange {
        if (!effectCanHitTarget(spellEffect.targets, caster, target)) {
            return DamageRange(0, 0)
        }
        val elementalDamageInfo = getElementalDamageInfo(spellEffect.effectType)
            ?: return DamageRange(0, 0)
        return DamageRange(
            minDamage = computeDamage(spellEffect.min, caster, target, elementalDamageInfo, criticalHit),
            maxDamage = computeDamage(spellEffect.max, caster, target, elementalDamageInfo, criticalHit),
        )
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
        val proximityMultiplier = if (caster.cell.neighbors.map { it.cellId }.contains(target.cell.cellId)) {
            DofusCharacteristics.MELEE_DAMAGE_DONE_PERCENT.getValue(caster, 100)
        } else {
            DofusCharacteristics.RANGED_DAMAGE_DONE_PERCENT.getValue(caster, 100)
        }
        val spellMultiplier = DofusCharacteristics.SPELL_DAMAGE_DONE_PERCENT.getValue(caster, 100)
        val finalDamageMultiplier = DofusCharacteristics.DEALT_DAMAGES_MULTIPLICATOR.getValue(caster, 100)
        val multipliedDamages = (damage.toFloat()
                * (proximityMultiplier.toFloat() / 100f)
                * (spellMultiplier.toFloat() / 100f)
                * (finalDamageMultiplier.toFloat() / 100f)).toInt()
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