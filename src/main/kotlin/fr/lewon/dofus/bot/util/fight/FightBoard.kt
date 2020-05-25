package fr.lewon.dofus.bot.util.fight

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.abs

class FightBoard(
    cells: List<FightCell>,
    val startCells: List<FightCell>,
    var yourPos: FightCell,
    var enemyPos: FightCell
) {

    val accessibleCells = ArrayList<FightCell>()
    val cellsByPosition = HashMap<Pair<Int, Int>, FightCell>()

    init {
        for (cell in cells) {
            cellsByPosition[Pair(cell.col, cell.row)] = cell
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

    fun lineOfSight(fromCell: FightCell, toCell: FightCell): Boolean {
        val x0 = fromCell.col
        val y0 = fromCell.row
        val x1 = toCell.col
        val y1 = toCell.row

        var clear = true
        var dx = abs(x1 - x0)
        var dy = abs(y1 - y0)
        var x = x0
        var y = y0
        var n = -1 + dx + dy
        val x_inc = if (x1 > x0) 1 else -1
        val y_inc = if (y1 > y0) 1 else -1
        var error = dx - dy
        dx *= 2
        dy *= 2

        for (i in 0..0) {
            if (error > 0) {
                x += x_inc
                error -= dy
            } else if (error < 0) {
                y += y_inc
                error += dx
            } else {
                x += x_inc
                error -= dy
                y += y_inc
                error += dx
                n--
            }
        }

        while (n > 0 && clear) {
            if (cellsByPosition[Pair(x, y)]?.fightCellType == FightCellType.WALL) {
                clear = false
            } else {
                when {
                    error > 0 -> {
                        x += x_inc
                        error -= dy
                    }
                    error < 0 -> {
                        y += y_inc
                        error += dx
                    }
                    else -> {
                        x += x_inc
                        error -= dy
                        y += y_inc
                        error += dx
                        n--
                    }
                }
                n--
            }
        }

        return clear
    }

    fun getDist(fromCell: FightCell, toCell: FightCell): Int? {
        return getPath(fromCell, toCell)?.size
    }

    fun getPath(fromCell: FightCell, toCell: FightCell): List<FightCell>? {
        if (fromCell == toCell) {
            return emptyList()
        }
        var node = findPath(fromCell, toCell) ?: return null
        val cells = LinkedList<FightCell>()
        while (node.parent != null) {
            cells.push(node.cell)
            node = node.parent!!
        }
        return cells
    }

    private fun findPath(fromCell: FightCell, toCell: FightCell): Node? {
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
                    if (!explored.contains(neighbor) && neighbor.fightCellType == FightCellType.ACCESSIBLE) {
                        explored.add(neighbor)
                        newFrontier.add(Node(node, neighbor))
                    }
                }
            }
            frontier = newFrontier
        }
        return null
    }

    fun cellsAtRange(range: Int, fromCell: FightCell): List<FightCell> {
        val explored = mutableListOf(fromCell)
        var frontier = listOf(fromCell)
        for (i in 0 until range) {
            val newFrontier = ArrayList<FightCell>()
            for (cell in frontier) {
                for (neighbor in cell.neighbors) {
                    if (!explored.contains(neighbor) && neighbor.fightCellType == FightCellType.ACCESSIBLE) {
                        explored.add(neighbor)
                        newFrontier.add(neighbor)
                    }
                }
            }
            frontier = newFrontier
        }
        return frontier
    }

    private class Node(val parent: Node?, val cell: FightCell)

    fun deepCopy(): FightBoard {
        val cells = cellsByPosition.values.map { it.deepCopy() }
        val startCellsCoordinates = startCells.map { Pair(it.col, it.row) }
        val startCells = cells.filter { startCellsCoordinates.contains(Pair(it.col, it.row)) }
        val yourPosCopy = cells.first { yourPos.col == it.col && yourPos.row == it.row }
        val enemyPosCopy = cells.first { enemyPos.col == it.col && enemyPos.row == it.row }
        val accessibleCellsCoordinates = startCells.map { Pair(it.col, it.row) }
        val accessibleCellsCopy = cells.filter { accessibleCellsCoordinates.contains(Pair(it.col, it.row)) }
        return FightBoard(cells, startCells, yourPosCopy, enemyPosCopy)
            .also { it.accessibleCells.addAll(accessibleCellsCopy) }
    }

}