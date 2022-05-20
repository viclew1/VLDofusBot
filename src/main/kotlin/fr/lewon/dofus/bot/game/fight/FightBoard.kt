package fr.lewon.dofus.bot.game.fight

import fr.lewon.dofus.bot.core.d2o.managers.entity.MonsterManager
import fr.lewon.dofus.bot.core.d2o.managers.spell.SpellManager
import fr.lewon.dofus.bot.core.model.spell.DofusSpell
import fr.lewon.dofus.bot.core.model.spell.DofusSpellLevel
import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.sniffer.model.types.fight.charac.CharacterCharacteristic
import fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.GameFightFighterInformations
import fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.ai.GameFightMonsterInformations
import fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.named.GameFightCharacterInformations
import fr.lewon.dofus.bot.util.network.GameInfo
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.abs

class FightBoard(private val gameInfo: GameInfo) {

    private val lock = ReentrantLock()
    private val dofusBoard = gameInfo.dofusBoard
    private val fightersById = HashMap<Double, Fighter>()

    fun move(fromCellId: Int, toCellId: Int) {
        try {
            lock.lockInterruptibly()
            getFighter(fromCellId)?.let { move(it, toCellId) }
        } finally {
            lock.unlock()
        }
    }

    fun move(fighterId: Double, toCellId: Int) {
        try {
            lock.lockInterruptibly()
            fightersById[fighterId]?.let { move(it, toCellId) }
        } finally {
            lock.unlock()
        }
    }

    fun move(fighter: Fighter, toCellId: Int) {
        try {
            lock.lockInterruptibly()
            move(fighter, dofusBoard.getCell(toCellId))
        } finally {
            lock.unlock()
        }
    }

    fun move(fighter: Fighter, toCell: DofusCell) {
        try {
            lock.lockInterruptibly()
            fighter.cell = toCell
        } finally {
            lock.unlock()
        }
    }

    fun resetFighters() {
        try {
            lock.lockInterruptibly()
            fightersById.clear()
        } finally {
            lock.unlock()
        }
    }

    fun killFighter(id: Double) {
        try {
            lock.lockInterruptibly()
            fightersById.remove(id)
        } finally {
            lock.unlock()
        }
    }

    fun createOrUpdateFighter(fighterInfo: GameFightFighterInformations): Fighter {
        try {
            lock.lockInterruptibly()
            val fighterId = fighterInfo.contextualId
            val cellId = fighterInfo.spawnInfo.informations.disposition.cellId
            val spells = getSpellLevels(gameInfo, fighterInfo, fighterId)
            val cell = dofusBoard.getCell(cellId)
            val fighter = fightersById.computeIfAbsent(fighterId) {
                Fighter(cell, fighterId, fighterInfo)
            }
            fighter.spells = spells
            fighter.teamId = fighterInfo.spawnInfo.teamId
            move(fighter, cell)
            return fighter
        } finally {
            lock.unlock()
        }
    }

    private fun getSpellLevels(
        gameInfo: GameInfo,
        fighterInfo: GameFightFighterInformations,
        fighterId: Double
    ): List<DofusSpellLevel> {
        return when {
            fighterInfo is GameFightMonsterInformations -> {
                val spells = MonsterManager.getMonster(fighterInfo.creatureGenericId.toDouble()).spells
                getSpellLevels(spells, fighterInfo.creatureLevel)
            }
            fighterInfo is GameFightCharacterInformations && fighterId == gameInfo.playerId -> {
                val spellIds = gameInfo.character.characterSpells.mapNotNull { it.spellId }
                val spells = spellIds.mapNotNull { SpellManager.getSpell(it) }
                getSpellLevels(spells, fighterInfo.level)
            }
            else -> emptyList()
        }
    }

    private fun getSpellLevels(spells: List<DofusSpell>, level: Int): List<DofusSpellLevel> {
        return spells.mapNotNull { getSpellLevel(it, level) }
    }

    private fun getSpellLevel(spell: DofusSpell, level: Int): DofusSpellLevel? {
        return spell.levels.filter { it.minPlayerLevel <= level }.maxByOrNull { it.minPlayerLevel }
    }

    fun updateFighterCharacteristics(fighterId: Double, characteristics: List<CharacterCharacteristic>) {
        try {
            lock.lockInterruptibly()
            fightersById[fighterId]?.let { updateFighterCharacteristics(it, characteristics) }
        } finally {
            lock.unlock()
        }
    }

    fun updateFighterCharacteristics(fighter: Fighter, characteristics: List<CharacterCharacteristic>) {
        try {
            lock.lockInterruptibly()
            characteristics.forEach { fighter.statsById[it.characteristicId] = it }
        } finally {
            lock.unlock()
        }
    }

    fun getPlayerFighter(): Fighter? {
        try {
            lock.lockInterruptibly()
            return fightersById[gameInfo.playerId]
        } finally {
            lock.unlock()
        }
    }

    fun getEnemyFighters(): List<Fighter> {
        try {
            lock.lockInterruptibly()
            return getFighters(true)
        } finally {
            lock.unlock()
        }
    }

    fun getClosestEnemy(): Fighter? {
        val playerFighter = getPlayerFighter() ?: return null
        return getEnemyFighters().minByOrNull { dofusBoard.getDist(playerFighter.cell, it.cell) }
    }

    fun getAlliedFighters(): List<Fighter> {
        try {
            lock.lockInterruptibly()
            return getFighters(false)
        } finally {
            lock.unlock()
        }
    }

    fun getAllFighters(withSummons: Boolean = true): List<Fighter> {
        try {
            lock.lockInterruptibly()
            return fightersById.values.toList().filter { withSummons || !it.isSummon() }
        } finally {
            lock.unlock()
        }
    }

    private fun getFighters(enemy: Boolean): List<Fighter> {
        return fightersById.values.filter { isFighterEnemy(it) == enemy }
    }

    fun isFighterEnemy(fighter: Fighter): Boolean {
        return getPlayerFighter()?.let {
            it.teamId != fighter.teamId
        } ?: true
    }

    fun isFighterHere(cell: DofusCell): Boolean {
        try {
            lock.lockInterruptibly()
            return getFighter(cell) != null
        } finally {
            lock.unlock()
        }
    }

    fun getFighter(cell: DofusCell): Fighter? {
        try {
            lock.lockInterruptibly()
            return getFighter(cell.cellId)
        } finally {
            lock.unlock()
        }
    }

    fun getFighter(cellId: Int): Fighter? {
        try {
            lock.lockInterruptibly()
            return fightersById.values.firstOrNull { it.cell.cellId == cellId }
        } finally {
            lock.unlock()
        }
    }

    fun getFighterById(fighterId: Double): Fighter? {
        try {
            lock.lockInterruptibly()
            return fightersById[fighterId]
        } finally {
            lock.unlock()
        }
    }

    fun lineOfSight(fromCell: Int, toCell: Int): Boolean {
        return lineOfSight(dofusBoard.getCell(fromCell), dofusBoard.getCell(toCell))
    }

    fun lineOfSight(fromCell: DofusCell, toCell: DofusCell): Boolean {
        try {
            lock.lockInterruptibly()
            val x0 = fromCell.col
            val y0 = fromCell.row
            val x1 = toCell.col
            val y1 = toCell.row

            var dx = abs(x1 - x0)
            var dy = abs(y1 - y0)
            var x = x0
            var y = y0
            var n = -1 + dx + dy
            val xInc = if (x1 > x0) 1 else -1
            val yInc = if (y1 > y0) 1 else -1
            var error = dx - dy
            dx *= 2
            dy *= 2

            when {
                error > 0 -> {
                    x += xInc
                    error -= dy
                }
                error < 0 -> {
                    y += yInc
                    error += dx
                }
                else -> {
                    x += xInc
                    error -= dy
                    y += yInc
                    error += dx
                    n--
                }
            }

            while (n > 0) {
                val cell = dofusBoard.getCell(x, y) ?: error("Cell [$x ; $y] does not exist")
                if (cell.isWall() || cell != fromCell && cell != toCell && isFighterHere(cell)) {
                    return false
                }
                when {
                    error > 0 -> {
                        x += xInc
                        error -= dy
                    }
                    error < 0 -> {
                        y += yInc
                        error += dx
                    }
                    else -> {
                        x += xInc
                        error -= dy
                        y += yInc
                        error += dx
                        n--
                    }
                }
                n--
            }
            return true
        } finally {
            lock.unlock()
        }
    }

    fun getMoveCellsWithMpUsed(range: Int, fromCell: DofusCell): List<Pair<DofusCell, Int>> {
        try {
            lock.lockInterruptibly()
            val cellsWithMpUsed = ArrayList<Pair<DofusCell, Int>>()
            cellsWithMpUsed.add(fromCell to 0)
            val accessibleCells = getMoveCellsWithMpUsed(range, listOf(fromCell))
            cellsWithMpUsed.addAll(accessibleCells)
            return cellsWithMpUsed
        } finally {
            lock.unlock()
        }
    }

    fun getMoveCellsWithMpUsed(range: Int, initialFrontier: List<DofusCell>): List<Pair<DofusCell, Int>> {
        try {
            lock.lockInterruptibly()
            val cellsWithMpUsed = ArrayList<Pair<DofusCell, Int>>()
            val explored = ArrayList<Int>()
            explored.addAll(initialFrontier.map { it.cellId })
            var frontier = ArrayList<DofusCell>()
            frontier.addAll(initialFrontier)
            var dist = 1
            for (i in 0 until range) {
                val newFrontier = ArrayList<DofusCell>()
                for (cell in frontier) {
                    for (neighbor in cell.neighbors) {
                        if (!explored.contains(neighbor.cellId) && !isFighterHere(neighbor) && neighbor.isAccessible()) {
                            explored.add(neighbor.cellId)
                            newFrontier.add(neighbor)
                            cellsWithMpUsed.add(neighbor to dist)
                        }
                    }
                }
                dist++
                frontier = newFrontier
            }
            return cellsWithMpUsed
        } finally {
            lock.unlock()
        }
    }

    fun deepCopy(): FightBoard {
        try {
            lock.lockInterruptibly()
            return FightBoard(gameInfo)
                .also { it.fightersById.putAll(fightersById.entries.map { e -> e.key to e.value.deepCopy() }) }
        } finally {
            lock.unlock()
        }
    }

}