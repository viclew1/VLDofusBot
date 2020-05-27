package fr.lewon.dofus.bot.util.fight

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.abs

class FightBoard(
    private val cells: List<FightCell>,
    val startCells: List<FightCell>,
    var yourPos: FightCell,
    var enemyPos: FightCell
) {

    val accessibleCells = ArrayList<FightCell>()
    private val cellsByPosition = HashMap<Pair<Int, Int>, FightCell>()

    fun init() {
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
            if (cellsByPosition[Pair(x, y)]?.fightCellType == FightCellType.WALL) {
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

    fun cellAt(x: Int, y: Int): FightCell {
        return cellsByPosition[Pair(x, y)] ?: error("Unknown move cell")
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
        return explored
    }

    private class Node(val parent: Node?, val cell: FightCell)

    fun clone(): FightBoard {
        return FightBoard(cells, startCells, yourPos, enemyPos)
    }

}