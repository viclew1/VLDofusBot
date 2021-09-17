package fr.lewon.dofus.bot.game.fight

import fr.lewon.dofus.bot.sniffer.model.types.fight.charac.CharacterCharacteristic
import fr.lewon.dofus.bot.util.geometry.RectangleRelative
import fr.lewon.dofus.bot.util.io.d2p.cell.CellData
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.abs

class FightBoard {

    companion object {
        private const val HEIGHT_RATIO = 0.885f
    }

    var startCells: List<FightCell> = ArrayList()
    var closestEnemyPosition: FightCell
    private val cellsByPosition = HashMap<Pair<Int, Int>, FightCell>()
    private val cellsByCellId = HashMap<Int, FightCell>()
    private val fightersById = HashMap<Double, Fighter>()

    init {
        val tileWidth = 1f / 14.5f
        val tileHeight = HEIGHT_RATIO / 20.5f
        val initialX = tileWidth / 2f
        val initialY = tileHeight / 4f

        val cells = ArrayList<FightCell>()
        for (xMultiplier in 0 until 28) {
            val x = initialX + (xMultiplier.toFloat() / 2f) * tileWidth
            for (yMultiplier in 0 until 20) {
                val row = -xMultiplier / 2 + yMultiplier
                val col = xMultiplier / 2 + xMultiplier % 2 + yMultiplier
                val cellId = xMultiplier / 2 + yMultiplier * 28 + xMultiplier % 2 * 14
                val y = initialY + yMultiplier * tileHeight + (xMultiplier % 2) * tileHeight / 2f
                val boundsX = x - tileWidth / 4f
                val boundsW = tileWidth / 2f
                val boundsH = tileHeight / 2f
                val bounds = RectangleRelative(boundsX, y, boundsW, boundsH)
                cells.add(FightCell(row, col, cellId, bounds))
            }
        }
        initNeighbors(cells)
        closestEnemyPosition = cells[0]
    }

    private fun initNeighbors(cells: List<FightCell>) {
        for (cell in cells) {
            cellsByPosition[Pair(cell.col, cell.row)] = cell
            cellsByCellId[cell.cellId] = cell
        }
        for (cell in cells) {
            val neighbors = listOfNotNull(
                cellsByPosition[Pair(cell.col - 1, cell.row)],
                cellsByPosition[Pair(cell.col + 1, cell.row)],
                cellsByPosition[Pair(cell.col, cell.row - 1)],
                cellsByPosition[Pair(cell.col, cell.row + 1)]
            )
            cell.neighbors.addAll(neighbors)
        }
    }

    fun debugPrintGrid() {
        for (row in 0 until 40) {
            if (row % 2 == 1) print("  ")
            for (col in 0 until 14) {
                val cellId = row * 14 + col
                val cell = getCell(cellId)
                val cellStr = when {
                    isFighterHere(cell) -> "F"
                    cell.isAccessible() -> "."
                    cell.isWall() -> "X"
                    else -> "O"
                }
                print("$cellStr   ")
            }
            println("")
        }
    }

    fun getCell(cellId: Int): FightCell {
        return cellsByCellId[cellId] ?: error("No cell with id")
    }

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
        move(fighter, getCell(toCellId), updateClosestEnemy)
    }

    fun move(fighter: Fighter, toCell: FightCell, updateClosestEnemy: Boolean = true) {
        fighter.fightCell = toCell
        if (updateClosestEnemy) updateClosestEnemy()
    }

    fun updateCells(cellDataList: List<CellData>) {
        for (cellData in cellDataList) {
            val fightCell = getCell(cellData.cellId)
            fightCell.cellData = cellData
        }
    }

    fun updateStartCells(positionsForChallengers: ArrayList<Int>) {
        startCells = positionsForChallengers.map { getCell(it) }
    }

    fun resetFighters() {
        fightersById.clear()
    }

    fun killFighter(id: Double) {
        fightersById.remove(id)
        updateClosestEnemy()
    }

    fun createOrUpdateFighter(fighterId: Double, cellId: Int) {
        val cell = getCell(cellId)
        val fighter = fightersById.computeIfAbsent(fighterId) { Fighter(cell, fighterId, !startCells.contains(cell)) }
        move(fighter, cell)
    }

    fun updateFighterCharacteristics(fighterId: Double, characteristics: List<CharacterCharacteristic>) {
        val fighter = fightersById[fighterId] ?: return
        characteristics.forEach { fighter.statsById[it.characteristicId] = it }
    }

    private fun updateClosestEnemy() {
        val playerFighter = getPlayerFighter() ?: return
        val enemyFighters = getEnemyFighters()
        closestEnemyPosition = enemyFighters.map { it.fightCell }
            .minBy { getPathLength(playerFighter.fightCell, it) ?: Int.MAX_VALUE }
            ?: getCell(0)
    }

    fun getPlayerFighter(): Fighter? {
        val alliedFighters = getAlliedFighters()
        if (alliedFighters.isEmpty()) {
            return null
        }
        return alliedFighters[0]
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

    fun isFighterHere(cell: FightCell): Boolean {
        return getFighter(cell) != null
    }

    fun getFighter(cell: FightCell): Fighter? {
        return getFighter(cell.cellId)
    }

    fun getFighter(cellId: Int): Fighter? {
        return fightersById.values.firstOrNull { it.fightCell.cellId == cellId }
    }

    fun lineOfSight(fromCell: FightCell, toCell: FightCell): Boolean {
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
            val cell = cellsByPosition[Pair(x, y)] ?: error("Cell [$x ; $y] does not exist")
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

    fun getPathLength(fromCell: FightCell, toCell: FightCell): Int? {
        return getPath(fromCell, toCell)?.size
    }

    fun getDist(fromCell: FightCell, toCell: FightCell): Int? {
        return getPath(fromCell, toCell, true)?.size
    }

    private fun getPath(fromCell: FightCell, toCell: FightCell, fly: Boolean = false): List<FightCell>? {
        if (fromCell == toCell) {
            return emptyList()
        }
        var node = findPath(fromCell, toCell, fly) ?: return null
        val cells = LinkedList<FightCell>()
        while (node.parent != null) {
            cells.push(node.cell)
            node = node.parent!!
        }
        return cells
    }

    private fun findPath(fromCell: FightCell, toCell: FightCell, fly: Boolean = false): Node? {
        val initialNode = Node(null, fromCell)
        val explored = mutableListOf(fromCell)
        var frontier = listOf(initialNode)
        while (frontier.isNotEmpty()) {
            val newFrontier = ArrayList<Node>()
            for (node in frontier) {
                if (node.cell == toCell) {
                    return node
                }
                for (neighbor in node.cell.neighbors) {
                    if (!explored.contains(neighbor) && (fly || neighbor.isAccessible())) {
                        explored.add(neighbor)
                        newFrontier.add(Node(node, neighbor))
                    }
                }
            }
            frontier = newFrontier
        }
        return null
    }

    fun getMoveCells(range: Int, fromCell: FightCell): List<FightCell> {
        val explored = mutableListOf(fromCell)
        var frontier = listOf(fromCell)
        for (i in 0 until range) {
            val newFrontier = ArrayList<FightCell>()
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

    fun cellsAtRange(minRange: Int, maxRange: Int, fromCell: FightCell): List<FightCell> {
        val cellsAtRange = ArrayList<FightCell>()
        val explored = mutableListOf(fromCell)
        var frontier = listOf(fromCell)

        if (minRange == 0) {
            cellsAtRange.add(fromCell)
        }

        for (i in 1..maxRange) {
            val newFrontier = ArrayList<FightCell>()
            for (cell in frontier) {
                for (neighbor in cell.neighbors) {
                    if (!explored.contains(neighbor)) {
                        if (i >= minRange) {
                            cellsAtRange.add(neighbor)
                        }
                        explored.add(neighbor)
                        newFrontier.add(neighbor)
                    }
                }
            }
            frontier = newFrontier
        }
        return cellsAtRange
    }

    private class Node(val parent: Node?, val cell: FightCell)

    fun clone(): FightBoard {
        return FightBoard()
            .also { it.cellsByPosition.putAll(cellsByPosition) }
            .also { it.cellsByCellId.putAll(cellsByCellId) }
            .also { it.fightersById.putAll(fightersById) }
            .also { it.closestEnemyPosition = closestEnemyPosition }
    }

}