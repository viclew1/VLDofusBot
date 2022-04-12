package fr.lewon.dofus.bot.game.fight.ai

import fr.lewon.dofus.bot.core.model.spell.*
import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.game.fight.DofusCharacteristics
import fr.lewon.dofus.bot.game.fight.FightBoard
import fr.lewon.dofus.bot.game.fight.Fighter
import fr.lewon.dofus.bot.sniffer.model.types.fight.charac.impl.CharacterCharacteristicValue
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

class SpellSimulator(val dofusBoard: DofusBoard) {

    private val damageCalculator = DamageCalculator()
    private val effectZoneCalculator = EffectZoneCalculator(dofusBoard)

    fun simulateSpell(fightBoard: FightBoard, caster: Fighter, spell: DofusSpellLevel, targetCellId: Int) {
        val criticalChance = spell.criticalHitProbability + DofusCharacteristics.CRITICAL_HIT.getValue(caster)
        val criticalHit = criticalChance >= 100
        val effects = if (criticalHit) spell.criticalEffects else spell.effects
        effects.forEach {
            simulateEffect(fightBoard, caster, it, targetCellId, criticalHit)
        }
    }

    private fun simulateEffect(
        fightBoard: FightBoard,
        caster: Fighter,
        effect: DofusSpellEffect,
        targetCellId: Int,
        criticalHit: Boolean
    ) {
        val casterCellId = caster.cell.cellId
        val targetCell = dofusBoard.getCell(targetCellId)
        val zone = effect.rawZone
        val affectedCells = effectZoneCalculator.getAffectedCells(casterCellId, targetCellId, zone)
        val fightersInAOE = affectedCells.mapNotNull { fightBoard.getFighter(it) }
            .filter { effectAffects(effect.target, caster, it) }
        val effectZoneType = effect.rawZone.effectZoneType
        when (effect.effectType) {
            DofusSpellEffectType.MP_BUFF ->
                simulateBuff(fightersInAOE, DofusCharacteristics.MOVEMENT_POINTS, effect.min)
            DofusSpellEffectType.CRITICAL_BUFF ->
                simulateBuff(fightersInAOE, DofusCharacteristics.CRITICAL_HIT, effect.min)
            DofusSpellEffectType.DAMAGE_BUFF ->
                simulateBuff(fightersInAOE, DofusCharacteristics.ALL_DAMAGES_BONUS, effect.min)
            DofusSpellEffectType.POWER_BUFF ->
                simulateBuff(fightersInAOE, DofusCharacteristics.DAMAGES_BONUS_PERCENT, effect.min)
            DofusSpellEffectType.DASH ->
                simulateDash(fightBoard, caster, targetCell, effect.min)
            DofusSpellEffectType.TELEPORT ->
                simulateTeleport(fightBoard, caster, targetCellId)
            DofusSpellEffectType.PUSH ->
                simulatePush(fightBoard, effectZoneType, caster, targetCellId, fightersInAOE, effect.min)
            DofusSpellEffectType.PULL ->
                simulatePull(fightBoard, effectZoneType, casterCellId, targetCellId, fightersInAOE, effect.min)
            DofusSpellEffectType.SWITCH_POSITIONS ->
                simulateSwitchPositions(fightBoard, caster, targetCellId)
            DofusSpellEffectType.AIR_DAMAGE, DofusSpellEffectType.EARTH_DAMAGE, DofusSpellEffectType.FIRE_DAMAGE, DofusSpellEffectType.NEUTRAL_DAMAGE, DofusSpellEffectType.WATER_DAMAGE ->
                simulateDamages(caster, targetCell, fightersInAOE, effect, criticalHit)
            DofusSpellEffectType.MP_DECREASED_EARTH_DAMAGE ->
                simulateMpDecreasedDamages(caster, targetCell, fightersInAOE, effect, criticalHit)
            DofusSpellEffectType.AIR_LIFE_STEAL, DofusSpellEffectType.EARTH_LIFE_STEAL, DofusSpellEffectType.FIRE_LIFE_STEAL, DofusSpellEffectType.NEUTRAL_LIFE_STEAL, DofusSpellEffectType.WATER_LIFE_STEAL ->
                simulateLifeSteal(caster, targetCell, fightersInAOE, effect, criticalHit)
        }
    }

    private fun effectAffects(spellTarget: DofusSpellTarget, caster: Fighter, target: Fighter): Boolean {
        return when (spellTarget) {
            DofusSpellTarget.EVERYBODY -> true
            DofusSpellTarget.ALLIES_ONLY -> caster.teamId == target.teamId
            DofusSpellTarget.ENEMIES_ONLY -> caster.teamId != target.teamId
        }
    }

    private fun simulateLifeSteal(
        caster: Fighter,
        aoeCenter: DofusCell,
        fightersInAOE: List<Fighter>,
        effect: DofusSpellEffect,
        criticalHit: Boolean
    ) {
        var dealtDamagesTotal = 0
        for (fighter in fightersInAOE) {
            val distToCenter = dofusBoard.getDist(fighter.cell, aoeCenter)
            val realDamage = (damageCalculator.getRealEffectDamage(
                effect, caster, fighter, criticalHit, false
            ).toFloat() * (1f - 0.1f * distToCenter)).toInt()
            dealtDamagesTotal += minOf(fighter.getCurrentHp(), realDamage)
            fighter.hpLost += realDamage
        }
        val maxHeal = caster.maxHp - caster.getCurrentHp()
        val heal = max(maxHeal, dealtDamagesTotal / 2)
        caster.hpHealed += heal
    }

    private fun simulateDamages(
        caster: Fighter,
        aoeCenter: DofusCell,
        fightersInAOE: List<Fighter>,
        effect: DofusSpellEffect,
        criticalHit: Boolean
    ) {
        for (fighter in fightersInAOE) {
            val distToCenter = dofusBoard.getDist(fighter.cell, aoeCenter)
            val realDamage = (damageCalculator.getRealEffectDamage(
                effect, caster, fighter, criticalHit, false
            ).toFloat() * (1f - 0.1f * distToCenter)).toInt()
            fighter.hpLost += realDamage
        }
    }

    private fun simulateMpDecreasedDamages(
        caster: Fighter,
        aoeCenter: DofusCell,
        fightersInAOE: List<Fighter>,
        effect: DofusSpellEffect,
        criticalHit: Boolean
    ) {
        val totalMp = caster.totalMp
        val mpUsed = caster.totalMp - DofusCharacteristics.MOVEMENT_POINTS.getValue(caster)
        if (totalMp > 0) {
            val mpUsedRatio = min(1f, max(0f, (totalMp - mpUsed).toFloat() / totalMp.toFloat()))
            for (fighter in fightersInAOE) {
                val distToCenter = dofusBoard.getDist(fighter.cell, aoeCenter)
                val realDamage = (damageCalculator.getRealEffectDamage(
                    effect, caster, fighter, criticalHit, false
                ).toFloat() * mpUsedRatio * (1f - 0.1f * distToCenter)).toInt()
                fighter.hpLost += realDamage
            }
        }
    }

    private fun simulateSwitchPositions(fightBoard: FightBoard, caster: Fighter, targetCellId: Int) {
        val target = fightBoard.getFighter(targetCellId) ?: return
        val oldCasterCellId = caster.cell.cellId
        fightBoard.move(caster, targetCellId)
        fightBoard.move(target, oldCasterCellId)
    }

    private fun simulateTeleport(fightBoard: FightBoard, caster: Fighter, targetCellId: Int) {
        fightBoard.move(caster.id, targetCellId)
    }

    private fun simulatePull(
        fightBoard: FightBoard,
        effectZoneType: DofusEffectZoneType,
        casterCellId: Int,
        targetCellId: Int,
        fightersInAOE: List<Fighter>,
        amount: Int
    ) {
        for (fighter in fightersInAOE) {
            val pullTowardCellId = getPushOriginCellId(casterCellId, targetCellId, fighter.cell.cellId, effectZoneType)
            val pullTowardCell = dofusBoard.getCell(pullTowardCellId)
            val pullDest = getRealDashDest(fightBoard, amount, fighter.cell, pullTowardCell)
            fightBoard.move(fighter, pullDest)
        }
    }

    private fun simulatePush(
        fightBoard: FightBoard,
        effectZoneType: DofusEffectZoneType,
        caster: Fighter,
        targetCellId: Int,
        fightersInAOE: List<Fighter>,
        amount: Int
    ) {
        for (fighter in fightersInAOE) {
            val pushFromCellId = getPushOriginCellId(
                caster.cell.cellId, targetCellId, fighter.cell.cellId, effectZoneType
            )
            val pushFromCell = dofusBoard.getCell(pushFromCellId)
            val pushDest = getRealDashDest(fightBoard, amount, fighter.cell, pushFromCell, true)
            val oldLoc = fighter.cell.cellId
            fightBoard.move(fighter, pushDest)
            val pushedDist = dofusBoard.getDist(oldLoc, fighter.cell.cellId)
            val doPouAmount = max(0, amount - pushedDist)
            if (doPouAmount > 0) {
                val level = DofusCharacteristics.LEVEL.getValue(caster).toFloat()
                val doPou = DofusCharacteristics.PUSH_DAMAGE_BONUS.getValue(caster).toFloat()
                val rePou = DofusCharacteristics.PUSH_DAMAGE_REDUCTION.getValue(fighter).toFloat()
                val pushDamage = ((level / 2f + doPou - rePou + 32f * doPouAmount.toFloat()) / 4f).toInt()
                fighter.hpLost += pushDamage
            }
        }
    }

    private fun getPushOriginCellId(
        casterCellId: Int,
        spellTargetCellId: Int,
        hitFighterCellId: Int,
        effectZoneType: DofusEffectZoneType
    ): Int {
        return if (effectZoneType != DofusEffectZoneType.CROSS_FROM_TARGET && spellTargetCellId == hitFighterCellId) {
            casterCellId
        } else {
            spellTargetCellId
        }
    }

    private fun simulateDash(fightBoard: FightBoard, caster: Fighter, targetCell: DofusCell, amount: Int) {
        val dashDest = getRealDashDest(fightBoard, amount, caster.cell, targetCell)
        fightBoard.move(caster.id, dashDest.cellId)
    }

    private fun simulateBuff(affectedFighters: List<Fighter>, characteristics: DofusCharacteristics, amount: Int) {
        for (fighter in affectedFighters) {
            val current = characteristics.getValue(fighter)
            fighter.statsById[characteristics.id] = CharacterCharacteristicValue().also {
                it.total = current + amount
            }
        }
    }

    private fun getRealDashDest(
        fightBoard: FightBoard,
        amount: Int,
        fromCell: DofusCell,
        toCell: DofusCell,
        invertDirection: Boolean = false
    ): DofusCell {
        val invertProduct = if (invertDirection) -1 else 1
        val dCol = toCell.col - fromCell.col
        val dRow = toCell.row - fromCell.row
        val sDCol = dCol.sign * invertProduct
        val sDRow = dRow.sign * invertProduct
        val absDCol = abs(dCol)
        val absDRow = abs(dRow)
        var destCell = fromCell
        val realAmount = if (absDCol == absDRow) amount / 2 else amount
        for (i in 0 until realAmount) {
            if (destCell.cellId == toCell.cellId) {
                break
            }
            val newDestCell = if (absDCol > absDRow) {
                dofusBoard.getCell(destCell.col + sDCol, destCell.row)
            } else if (absDCol < absDRow) {
                dofusBoard.getCell(destCell.col, destCell.row + sDRow)
            } else {
                val alignedCell1 =
                    dofusBoard.getCell(destCell.col + sDCol, destCell.row)
                val alignedCell2 =
                    dofusBoard.getCell(destCell.col, destCell.row + sDRow)
                if (alignedCell1 != null && alignedCell2 != null
                    && alignedCell1.isAccessible() && alignedCell2.isAccessible()
                    && !fightBoard.isFighterHere(alignedCell1) && !fightBoard.isFighterHere(alignedCell2)
                ) {
                    dofusBoard.getCell(destCell.col + sDCol, destCell.row + sDRow)
                } else {
                    null
                }
            }
            destCell = newDestCell?.takeIf { it.isAccessible() && !fightBoard.isFighterHere(it) }
                ?: break
        }
        return destCell
    }

}