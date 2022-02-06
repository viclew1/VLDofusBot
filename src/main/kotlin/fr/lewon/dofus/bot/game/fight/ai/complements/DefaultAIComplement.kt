package fr.lewon.dofus.bot.game.fight.ai.complements

import fr.lewon.dofus.bot.core.model.spell.DofusSpellLevel
import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.fight.DofusCharacteristics
import fr.lewon.dofus.bot.game.fight.FightBoard
import fr.lewon.dofus.bot.game.fight.Fighter
import fr.lewon.dofus.bot.game.fight.ai.DamageCalculator
import kotlin.math.min

class DefaultAIComplement(
    private val canAttack: Boolean = true,
    private val canMove: Boolean = true,
    private val shouldUseAllMP: Boolean = false,
) : AIComplement() {

    private var spellMaxRange: Int? = null

    override fun canAttack(playerFighter: Fighter): Boolean {
        return canAttack
    }

    override fun canMove(playerFighter: Fighter): Boolean {
        return canMove
    }

    override fun mustUseAllMP(playerFighter: Fighter): Boolean {
        return shouldUseAllMP
    }

    override fun getIdealDistance(playerFighter: Fighter, spells: List<DofusSpellLevel>, playerRange: Int): Int {
        return 5
    }

    private fun getSpellMaxRange(spells: List<DofusSpellLevel>, playerRange: Int): Int? {
        return spells.map { getSpellMaxRange(it, playerRange) }.maxOrNull()
    }

    private fun getSpellMaxRange(spell: DofusSpellLevel, playerRange: Int): Int {
        return if (spell.rangeCanBeBoosted) {
            spell.maxRange + playerRange
        } else spell.maxRange
    }

    override fun shouldAvoidUsingMp(): Boolean {
        return true
    }

    override fun buildDangerByCell(dofusBoard: DofusBoard, fightBoard: FightBoard): Map<Int, Int> {
        val dangerByCell = HashMap<Int, Int>()
        val playerFighter = fightBoard.getPlayerFighter()
            ?: error("Player fighter not found")
        fightBoard.getAlliedFighters().forEach { fightBoard.killFighter(it.id) }
        val damageCalculator = DamageCalculator()
        for (enemy in fightBoard.getEnemyFighters()) {
            computeEnemyDangerCells(dofusBoard, fightBoard, enemy, playerFighter, damageCalculator, dangerByCell)
        }
        return dangerByCell
    }

    private fun computeEnemyDangerCells(
        dofusBoard: DofusBoard,
        fightBoard: FightBoard,
        enemy: Fighter,
        playerFighter: Fighter,
        damageCalculator: DamageCalculator,
        dangerByCell: HashMap<Int, Int>
    ) {
        val mp = min(10, DofusCharacteristics.MOVEMENT_POINTS.getValue(enemy))
        val accessibleCells = fightBoard.getMoveCellsWithMpUsed(mp, enemy.cell)
            .map { it.first }
        for (spell in enemy.spells) {
            val realDamage = damageCalculator.getRealDamage(spell, enemy, playerFighter, upperBound = true)
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