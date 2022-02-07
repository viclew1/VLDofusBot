package fr.lewon.dofus.bot.game.fight.ai

import fr.lewon.dofus.bot.core.model.spell.DofusSpellEffect
import fr.lewon.dofus.bot.core.model.spell.DofusSpellEffectType
import fr.lewon.dofus.bot.core.model.spell.DofusSpellLevel
import fr.lewon.dofus.bot.core.model.spell.DofusSpellTarget
import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.game.fight.DofusCharacteristics
import fr.lewon.dofus.bot.game.fight.FightBoard
import fr.lewon.dofus.bot.game.fight.Fighter
import fr.lewon.dofus.bot.sniffer.model.types.fight.charac.impl.CharacterCharacteristicValue
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sign

class SpellSimulator(val dofusBoard: DofusBoard) {

    private val damageCalculator = DamageCalculator()
    private val effectZoneCalculator = EffectZoneCalculator(dofusBoard)

    fun simulateSpell(fightBoard: FightBoard, caster: Fighter, spell: DofusSpellLevel, targetCellId: Int) {
        val criticalChance = spell.criticalHitProbability + DofusCharacteristics.CRITICAL_HIT.getValue(caster)
        val criticalHit = criticalChance > 80
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
            .distinct()
        when (effect.effectType) {
            DofusSpellEffectType.MP_BUFF ->
                simulateMpBuff(fightersInAOE, effect.min)
            DofusSpellEffectType.DASH ->
                simulateDash(fightBoard, caster, targetCell, effect.min)
            DofusSpellEffectType.TELEPORT ->
                simulateTeleport(fightBoard, caster, targetCellId)
            DofusSpellEffectType.PUSH ->
                simulatePush(fightBoard, caster, targetCellId, fightersInAOE, effect.min)
            DofusSpellEffectType.PULL ->
                simulatePull(fightBoard, casterCellId, targetCellId, fightersInAOE, effect.min)
            DofusSpellEffectType.SWITCH_POSITIONS ->
                simulateSwitchPositions(fightBoard, caster, targetCellId)
            DofusSpellEffectType.AIR_DAMAGE, DofusSpellEffectType.EARTH_DAMAGE, DofusSpellEffectType.FIRE_DAMAGE, DofusSpellEffectType.NEUTRAL_DAMAGE, DofusSpellEffectType.WATER_DAMAGE ->
                simulateDamages(caster, targetCell, fightersInAOE, effect, criticalHit)
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
        casterCellId: Int,
        targetCellId: Int,
        fightersInAOE: List<Fighter>,
        amount: Int
    ) {
        for (fighter in fightersInAOE) {
            val pullTowardCellId = if (targetCellId == fighter.cell.cellId) casterCellId else targetCellId
            val pullTowardCell = dofusBoard.getCell(pullTowardCellId)
            val pullDest = getRealDashDest(fightBoard, amount, fighter.cell, pullTowardCell)
            fightBoard.move(fighter, pullDest)
        }
    }

    private fun simulatePush(
        fightBoard: FightBoard,
        caster: Fighter,
        targetCellId: Int,
        fightersInAOE: List<Fighter>,
        amount: Int
    ) {
        for (fighter in fightersInAOE) {
            val pushFromCellId = if (targetCellId == fighter.cell.cellId) caster.cell.cellId else targetCellId
            val pushFromCell = dofusBoard.getCell(pushFromCellId)
            val pushDest = getRealDashDest(fightBoard, amount, fighter.cell, pushFromCell, true)
            val oldLoc = fighter.cell
            fightBoard.move(fighter, pushDest)
            val pushedDist = dofusBoard.getDist(oldLoc, pushFromCell)
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

    private fun simulateDash(fightBoard: FightBoard, caster: Fighter, targetCell: DofusCell, amount: Int) {
        val dashDest = getRealDashDest(fightBoard, amount, caster.cell, targetCell)
        fightBoard.move(caster.id, dashDest.cellId)
    }

    private fun simulateMpBuff(affectedFighters: List<Fighter>, amount: Int) {
        for (fighter in affectedFighters) {
            val currentMp = DofusCharacteristics.MOVEMENT_POINTS.getValue(fighter)
            fighter.statsById[DofusCharacteristics.MOVEMENT_POINTS.id] = CharacterCharacteristicValue().also {
                it.total = currentMp + amount
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