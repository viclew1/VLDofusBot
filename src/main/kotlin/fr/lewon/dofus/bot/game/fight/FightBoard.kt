package fr.lewon.dofus.bot.game.fight

import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.sniffer.model.types.fight.charac.CharacterCharacteristic
import fr.lewon.dofus.bot.util.network.GameInfo
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.abs

class FightBoard(private val gameInfo: GameInfo) {

    private val lock = ReentrantLock()
    private val dofusBoard = gameInfo.dofusBoard
    var closestEnemyPosition = dofusBoard.cells[0]
    private val fightersById = HashMap<Double, Fighter>()

    fun move(fromCellId: Int, toCellId: Int, updateClosestEnemy: Boolean = true) {
        lock.lock()
        getFighter(fromCellId)?.let { move(it, toCellId, updateClosestEnemy) }
        lock.unlock()
    }

    fun move(fighterId: Double, toCellId: Int, updateClosestEnemy: Boolean = true) {
        lock.lock()
        fightersById[fighterId]?.let { move(it, toCellId, updateClosestEnemy) }
        lock.unlock()
    }

    fun move(fighter: Fighter, toCellId: Int, updateClosestEnemy: Boolean = true) {
        lock.lock()
        move(fighter, dofusBoard.getCell(toCellId), updateClosestEnemy)
        lock.unlock()
    }

    fun move(fighter: Fighter, toCell: DofusCell, updateClosestEnemy: Boolean = true) {
        lock.lock()
        fighter.cell = toCell
        if (updateClosestEnemy) updateClosestEnemy()
        lock.unlock()
    }

    fun resetFighters() {
        lock.lock()
        fightersById.clear()
        lock.unlock()
    }

    fun killFighter(id: Double) {
        lock.lock()
        fightersById.remove(id)
        updateClosestEnemy()
        lock.unlock()
    }

    fun createOrUpdateFighter(fighterId: Double, cellId: Int) {
        lock.lock()
        val cell = dofusBoard.getCell(cellId)
        val fighter = fightersById.computeIfAbsent(fighterId) {
            Fighter(cell, fighterId, !dofusBoard.startCells.contains(cell))
        }
        move(fighter, cell)
        lock.unlock()
    }

    fun updateFighterCharacteristics(fighterId: Double, characteristics: List<CharacterCharacteristic>) {
        lock.lock()
        fightersById[fighterId]?.let { updateFighterCharacteristics(it, characteristics) }
        lock.unlock()
    }

    fun updateFighterCharacteristics(fighter: Fighter, characteristics: List<CharacterCharacteristic>) {
        lock.lock()
        characteristics.forEach { fighter.statsById[it.characteristicId] = it }
        lock.unlock()
    }

    private fun updateClosestEnemy() {
        lock.lock()
        val playerFighter = getPlayerFighter()
        if (playerFighter != null) {
            val enemyFighters = getEnemyFighters()
            closestEnemyPosition = enemyFighters.map { it.cell }
                .minByOrNull { dofusBoard.getPathLength(playerFighter.cell, it) ?: Int.MAX_VALUE }
                ?: dofusBoard.getCell(0)
        }
        lock.unlock()
    }

    fun getPlayerFighter(): Fighter? {
        lock.lock()
        return getAlliedFighters().firstOrNull { it.id == gameInfo.playerId }
            .also { lock.unlock() }
    }

    fun getEnemyFighters(): List<Fighter> {
        lock.lock()
        return getFighters(true).also { lock.unlock() }
    }

    fun getAlliedFighters(): List<Fighter> {
        lock.lock()
        return getFighters(false).also { lock.unlock() }
    }

    private fun getFighters(enemy: Boolean): List<Fighter> {
        return fightersById.values.filter { it.enemy == enemy }
    }

    fun isFighterHere(cell: DofusCell): Boolean {
        lock.lock()
        val isFighterHere = getFighter(cell) != null
        lock.unlock()
        return isFighterHere
    }

    fun getFighter(cell: DofusCell): Fighter? {
        lock.lock()
        return getFighter(cell.cellId).also { lock.unlock() }
    }

    fun getFighter(cellId: Int): Fighter? {
        lock.lock()
        return fightersById.values.firstOrNull { it.cell.cellId == cellId }
            .also { lock.unlock() }
    }

    fun getFighterById(fighterId: Double): Fighter? {
        lock.lock()
        val fighter = fightersById[fighterId]
        lock.unlock()
        return fighter
    }

    fun lineOfSight(fromCell: DofusCell, toCell: DofusCell): Boolean {
        lock.lock()
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
                lock.unlock()
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

        lock.unlock()
        return true
    }

    fun getMoveCells(range: Int, fromCell: DofusCell): List<DofusCell> {
        lock.lock()
        return getMoveCells(range, listOf(fromCell))
            .also { lock.unlock() }
    }

    fun getMoveCells(range: Int, initialFrontier: List<DofusCell>): List<DofusCell> {
        lock.lock()
        val explored = ArrayList(initialFrontier)
        var frontier = initialFrontier
        for (i in 0 until range) {
            val newFrontier = ArrayList<DofusCell>()
            for (cell in frontier) {
                for (neighbor in cell.neighbors) {
                    if (!explored.contains(neighbor) && !isFighterHere(neighbor) && neighbor.isAccessible()) {
                        explored.add(neighbor)
                        newFrontier.add(neighbor)
                    }
                }
            }
            frontier = newFrontier
        }
        lock.unlock()
        return explored
    }

    fun clone(): FightBoard {
        lock.lock()
        return FightBoard(gameInfo)
            .also { it.closestEnemyPosition = closestEnemyPosition }
            .also { it.fightersById.putAll(fightersById.entries.map { e -> e.key to e.value.clone() }) }
            .also { lock.unlock() }
    }

}