package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.manager.d2p.maps.cell.CellData
import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.game.fight.FightCell
import fr.lewon.dofus.bot.game.move.MoveHistory
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.geometry.PointRelative

abstract class MoveTask(private val direction: Direction) : BooleanDofusBotTask() {

    override fun onStarted(): String {
        return "Moving toward [$direction] ..."
    }

    override fun execute(logItem: LogItem): Boolean {
        val fromMap = GameInfo.currentMap
        val destCellId = getMoveCellId()
            ?: return false
        val destCell = GameInfo.fightBoard.getCell(destCellId)
        val destCellCenter = destCell.getCenter()
        val clickLoc = PointRelative(getOverrideX() ?: destCellCenter.x, getOverrideY() ?: destCellCenter.y)
        if (!MoveUtil.processMove(clickLoc)) {
            return false
        }
        MoveHistory.addMove(direction, fromMap, GameInfo.currentMap)
        return true
    }

    private fun getMoveCellId(): Int? {
        val playerCellId = GameInfo.entityPositionsOnMapByEntityId[GameInfo.playerId]
            ?: error("No registered position for player")
        val moveCells = GameInfo.fightBoard.cells
            .filter { isCellOk(it.cellData) }
            .map { it.cellId }
        val invalidMoveCells = GameInfo.entityPositionsOnMapByEntityId.values
            .flatMap { GameInfo.fightBoard.cellsAtRange(0, 3, it) }
            .map { it.cellId }
        val validMoveCells = moveCells
            .filter { !invalidMoveCells.contains(it) }
        return getClosestCellId(playerCellId, validMoveCells)
    }

    private fun getClosestCellId(fromCellId: Int, validMoveCellIds: List<Int>): Int? {
        if (validMoveCellIds.contains(fromCellId)) {
            return fromCellId
        }
        val fromCell = GameInfo.fightBoard.getCell(fromCellId)
        val explored = mutableListOf(fromCell)
        var frontier = listOf(fromCell)
        while (frontier.isNotEmpty()) {
            val newFrontier = ArrayList<FightCell>()
            for (cell in frontier) {
                for (neighbor in cell.neighbors.union(cell.diagonalNeighbors)) {
                    if (validMoveCellIds.contains(neighbor.cellId)) {
                        return neighbor.cellId
                    }
                    if (!explored.contains(neighbor) && neighbor.cellData.mov) {
                        explored.add(neighbor)
                        newFrontier.add(neighbor)
                    }
                }
            }
            frontier = newFrontier
        }
        return null
    }

    protected abstract fun getOverrideX(): Float?

    protected abstract fun getOverrideY(): Float?

    protected abstract fun isCellOk(cellData: CellData): Boolean

}