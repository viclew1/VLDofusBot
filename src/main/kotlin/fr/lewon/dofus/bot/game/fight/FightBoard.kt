package fr.lewon.dofus.bot.game.fight

import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.sniffer.model.types.fight.charac.CharacterCharacteristic
import fr.lewon.dofus.bot.util.network.GameInfo
import kotlin.math.abs

class FightBoard(private val gameInfo: GameInfo) {

    private val dofusBoard = gameInfo.dofusBoard
    var closestEnemyPosition = dofusBoard.cells[0]
    private val fightersById = HashMap<Double, Fighter>()

    fun move(fromCellId: Int, toCellId: Int, updateClosestEnemy: Boolean = true) {
        val fighter = getFighter(fromCellId)
        if (fighter != null) {
            move(fighter, toCellId, updateClosestEnemy)
        }
    }

    fun move(fighterId: Double, toCellId: Int, updateClosestEnemy: Boolean = true) {
        val fighter = fightersById[fighterId]
        if (fighter != null) {
            move(fighter, toCellId, updateClosestEnemy)
        }
    }

    fun move(fighter: Fighter, toCellId: Int, updateClosestEnemy: Boolean = true) {
        move(fighter, dofusBoard.getCell(toCellId), updateClosestEnemy)
    }

    fun move(fighter: Fighter, toCell: DofusCell, updateClosestEnemy: Boolean = true) {
        fighter.cell = toCell
        if (updateClosestEnemy) updateClosestEnemy()
    }

    fun resetFighters() {
        fightersById.clear()
    }

    fun killFighter(id: Double) {
        fightersById.remove(id)
        updateClosestEnemy()
    }

    fun createOrUpdateFighter(fighterId: Double, cellId: Int) {
        val cell = dofusBoard.getCell(cellId)
        val fighter = fightersById.computeIfAbsent(fighterId) {
            Fighter(cell, fighterId, !dofusBoard.startCells.contains(cell))
        }
        move(fighter, cell)
    }

    fun updateFighterCharacteristics(fighterId: Double, characteristics: List<CharacterCharacteristic>) {
        val fighter = fightersById[fighterId] ?: return
        updateFighterCharacteristics(fighter, characteristics)
    }

    fun updateFighterCharacteristics(fighter: Fighter, characteristics: List<CharacterCharacteristic>) {
        characteristics.forEach { fighter.statsById[it.characteristicId] = it }
    }

    private fun updateClosestEnemy() {
        val playerFighter = getPlayerFighter() ?: return
        val enemyFighters = getEnemyFighters()
        closestEnemyPosition = enemyFighters.map { it.cell }
            .minByOrNull { dofusBoard.getPathLength(playerFighter.cell, it) ?: Int.MAX_VALUE }
            ?: dofusBoard.getCell(0)
    }

    fun getPlayerFighter(): Fighter? {
        return getAlliedFighters().firstOrNull { it.id == gameInfo.playerId }
    }

    fun getEnemyFighters(): List<Fighter> {
        return getFighters(true)
    }

    fun getAlliedFighters(): List<Fighter> {
        return getFighters(false)
    }

    private fun getFighters(enemy: Boolean): List<Fighter> {
        return fightersById.values.filter { it.enemy == enemy }
    }

    fun isFighterHere(cell: DofusCell): Boolean {
        return getFighter(cell) != null
    }

    fun getFighter(cell: DofusCell): Fighter? {
        return getFighter(cell.cellId)
    }

    fun getFighter(cellId: Int): Fighter? {
        return fightersById.values.firstOrNull { it.cell.cellId == cellId }
    }

    fun getFighterById(fighterId: Double): Fighter? {
        return fightersById[fighterId]
    }

    fun lineOfSight(fromCell: DofusCell, toCell: DofusCell): Boolean {
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
    }

    fun getMoveCells(range: Int, fromCell: DofusCell): List<DofusCell> {
        return getMoveCells(range, listOf(fromCell))
    }

    fun getMoveCells(range: Int, initialFrontier: List<DofusCell>): List<DofusCell> {
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
        return explored
    }

    fun clone(): FightBoard {
        return FightBoard(gameInfo)
            .also { it.closestEnemyPosition = closestEnemyPosition }
            .also { it.fightersById.putAll(fightersById.entries.map { e -> e.key to e.value.clone() }) }
    }

}