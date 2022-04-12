package fr.lewon.dofus.bot.game.fight.ai

import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.fight.DofusCharacteristics
import fr.lewon.dofus.bot.game.fight.FightBoard
import fr.lewon.dofus.bot.game.fight.Fighter
import kotlin.math.min

class DangerMap(
    private val damageCalculator: DamageCalculator = DamageCalculator()
) : HashMap<Double, HashMap<Int, Int>>() {

    fun getCellDanger(cellId: Int): Int {
        return values.sumOf { it[cellId] ?: 0 }
    }

    fun deepCopy(): DangerMap {
        val newDangerMap = DangerMap(damageCalculator)
        entries.forEach {
            newDangerMap[it.key] = HashMap(it.value)
        }
        return newDangerMap
    }

    fun recalculateDanger(
        dofusBoard: DofusBoard,
        fightBoard: FightBoard,
        playerFighter: Fighter,
        enemyFighter: Fighter
    ) {
        fightBoard.getAllFighters().filter { it.id != enemyFighter.id }
            .forEach { fightBoard.killFighter(it.id) }
        val mp = min(10, DofusCharacteristics.MOVEMENT_POINTS.getValue(enemyFighter))
        val accessibleCells = fightBoard.getMoveCellsWithMpUsed(mp, enemyFighter.cell)
            .map { it.first }
        val dangerByCell = computeIfAbsent(enemyFighter.id) { HashMap() }
        for (spell in enemyFighter.spells) {
            val realDamage = spell.effects
                .sumOf { damageCalculator.getRealEffectDamage(it, enemyFighter, playerFighter, false, true) }
            val cellsWithLos = dofusBoard.cellsAtRange(spell.minRange, spell.maxRange, accessibleCells)
                .filter { accessibleCells.any { ac -> fightBoard.lineOfSight(it.first, ac) } }
                .map { it.first }
            for (cell in cellsWithLos) {
                val currentDanger = dangerByCell[cell.cellId] ?: 0
                dangerByCell[cell.cellId] = currentDanger + realDamage
            }
        }
    }

}