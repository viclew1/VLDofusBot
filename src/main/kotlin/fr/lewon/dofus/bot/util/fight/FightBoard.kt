package fr.lewon.dofus.bot.util.fight

import java.util.*
import kotlin.collections.ArrayList

class FightBoard(
    val cells: List<FightCell>,
    val startCells: List<FightCell>,
    var yourPos: FightCell,
    var enemyPos: FightCell
) {

    val accessibleCells = ArrayList<FightCell>()

    init {
        for (c1 in cells) {
            for (c2 in cells) {
                if (c1 != c2 && c1.bounds.intersects(c2.bounds)) {
                    c1.neighbors.add(c2)
                    c2.neighbors.add(c1)
                }
            }
        }
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
                    if (!explored.contains(neighbor)) {
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
                    if (!explored.contains(neighbor)) {
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

}