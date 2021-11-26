package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.manager.d2o.D2OUtil
import fr.lewon.dofus.bot.core.manager.d2p.maps.cell.CellData
import fr.lewon.dofus.bot.core.manager.ui.UIPoint
import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.game.move.MoveHistory
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.custom.InteractiveMoveTask
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.ConverterUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.GameInfo
import kotlin.math.abs

abstract class MoveTask(
    private val direction: Direction,
    private val linkedZoneCellId: Int? = null
) : BooleanDofusBotTask() {

    override fun onStarted(): String {
        return "Moving toward [$direction] ..."
    }

    override fun execute(logItem: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken): Boolean {
        val fromMap = gameInfo.currentMap
        val moveCellId = getMoveCellId(gameInfo, cancellationToken)
        var moveDone = false
        if (moveCellId != null) {
            val clickLoc = getStandardClickLoc(gameInfo, moveCellId)
            if (!MoveUtil.processMove(gameInfo, clickLoc, cancellationToken)) {
                return false
            }
            moveDone = true
        }
        if (!moveDone) {
            val elementId = getMoveElementId(gameInfo)
            if (elementId != null) {
                if (!InteractiveMoveTask(elementId).run(logItem, gameInfo, cancellationToken)) {
                    return false
                }
                moveDone = true
            }
        }
        if (!moveDone) {
            return false
        }

        MoveHistory.addMove(direction, fromMap, gameInfo.currentMap)
        return true
    }

    private fun getStandardClickLoc(gameInfo: GameInfo, destCellId: Int): PointRelative {
        val destCell = gameInfo.dofusBoard.getCell(destCellId)
        val destCellCenter = destCell.getCenter()
        val floor = if (destCellId == getDefaultMoveCell()) 0 else destCell.cellData.floor
        val dFloor = ConverterUtil.toPointRelative(UIPoint(y = floor.toFloat()))
        return PointRelative(getOverrideX() ?: destCellCenter.x, getOverrideY() ?: (destCellCenter.y - dFloor.y))
    }

    private fun getMoveElementId(gameInfo: GameInfo): Int? {
        val skillsByElements = gameInfo.interactiveElements
            .associateWith { it.enabledSkills.mapNotNull { s -> D2OUtil.getObject("Skills", s.skillId.toDouble()) } }
        return skillsByElements.entries.firstOrNull { it.value.firstOrNull { s -> s["elementActionId"] == 16 } != null }
            ?.key?.elementId
    }

    private fun getMoveCellId(gameInfo: GameInfo, cancellationToken: CancellationToken): Int? {
        var playerCellId = gameInfo.entityPositionsOnMapByEntityId[gameInfo.playerId]
        if (playerCellId == null) {
            WaitUtil.waitUntil(
                { gameInfo.entityPositionsOnMapByEntityId[gameInfo.playerId] != null },
                cancellationToken
            )
            playerCellId = gameInfo.entityPositionsOnMapByEntityId[gameInfo.playerId]
                ?: error("No registered position for player")
        }
        val moveCells = getMoveCellIds(gameInfo)
        if (moveCells.isEmpty()) {
            return null
        }
        val invalidMoveCells = gameInfo.entityPositionsOnMapByEntityId.values
            .flatMap { getInvalidCellsNearEntity(gameInfo, it) }
            .map { it.cellId }
        val validMoveCells = moveCells
            .filter { !invalidMoveCells.contains(it) }
        if (validMoveCells.isEmpty()) {
            return getDefaultMoveCell()
        }
        return getClosestCellId(gameInfo, playerCellId, validMoveCells)
            ?: getDefaultMoveCell()
    }

    private fun getInvalidCellsNearEntity(gameInfo: GameInfo, entityCellId: Int): List<DofusCell> {
        val entityCell = gameInfo.dofusBoard.getCell(entityCellId)
        val invalidCells = mutableListOf(entityCell)
        val row = entityCell.row
        val col = entityCell.col
        for (i in 0 until 4) {
            gameInfo.dofusBoard.getCell(col - 1 - i, row - i)?.let { invalidCells.add(it) }
            gameInfo.dofusBoard.getCell(col - i, row - 1 - i)?.let { invalidCells.add(it) }
            gameInfo.dofusBoard.getCell(col - 1 - i, row - 1 - i)?.let { invalidCells.add(it) }
        }
        gameInfo.dofusBoard.getCell(col + 1, row)?.let { invalidCells.add(it) }
        gameInfo.dofusBoard.getCell(col, row + 1)?.let { invalidCells.add(it) }
        return invalidCells
    }

    private fun getMoveCellIds(gameInfo: GameInfo): List<Int> {
        if (linkedZoneCellId == null) {
            return gameInfo.dofusBoard.cells.filter { isCellOk(it.cellData) }.map { it.cellId }
        }
        val fromCell = gameInfo.dofusBoard.getCell(linkedZoneCellId)
        val explored = mutableListOf(fromCell.cellId)
        var frontier = listOf(fromCell)
        while (frontier.isNotEmpty()) {
            val newFrontier = ArrayList<DofusCell>()
            for (cell in frontier) {
                for (neighbor in getAccessibleCellsAround(gameInfo, cell)) {
                    if (!explored.contains(neighbor.cellId) && isCellOk(neighbor.cellData)
                    ) {
                        explored.add(neighbor.cellId)
                        newFrontier.add(neighbor)
                    }
                }
            }
            frontier = newFrontier
        }
        return explored
    }

    private fun getClosestCellId(gameInfo: GameInfo, fromCellId: Int, validMoveCellIds: List<Int>): Int? {
        if (validMoveCellIds.contains(fromCellId)) {
            return fromCellId
        }
        val fromCell = gameInfo.dofusBoard.getCell(fromCellId)
        val explored = mutableListOf(fromCell)
        var frontier = listOf(fromCell)
        while (frontier.isNotEmpty()) {
            val newFrontier = ArrayList<DofusCell>()
            for (cell in frontier) {
                for (neighbor in getAccessibleCellsAround(gameInfo, cell)) {
                    if (validMoveCellIds.contains(neighbor.cellId)) {
                        return neighbor.cellId
                    }
                    if (!explored.contains(neighbor)) {
                        explored.add(neighbor)
                        newFrontier.add(neighbor)
                    }
                }
            }
            frontier = newFrontier
        }
        return null
    }

    private fun getAccessibleCellsAround(gameInfo: GameInfo, cell: DofusCell): Iterable<DofusCell> {
        val dofusBoard = gameInfo.dofusBoard
        val col = cell.col
        val row = cell.row
        val tlNeighbor = dofusBoard.getCell(col - 1, row)?.takeIf { isCellMoveValid(cell, it) }
        val trNeighbor = dofusBoard.getCell(col, row - 1)?.takeIf { isCellMoveValid(cell, it) }
        val blNeighbor = dofusBoard.getCell(col, row + 1)?.takeIf { isCellMoveValid(cell, it) }
        val brNeighbor = dofusBoard.getCell(col + 1, row)?.takeIf { isCellMoveValid(cell, it) }
        val tDiagonalNeighbor = if (tlNeighbor != null || trNeighbor != null) {
            dofusBoard.getCell(col - 1, row - 1)?.takeIf { isCellMoveValid(cell, it) }
        } else null
        val rDiagonalNeighbor = if (brNeighbor != null || trNeighbor != null) {
            dofusBoard.getCell(col + 1, row - 1)?.takeIf { isCellMoveValid(cell, it) }
        } else null
        val bDiagonalNeighbor = if (blNeighbor != null || brNeighbor != null) {
            dofusBoard.getCell(col + 1, row + 1)?.takeIf { isCellMoveValid(cell, it) }
        } else null
        val lDiagonalNeighbor = if (tlNeighbor != null || blNeighbor != null) {
            dofusBoard.getCell(col - 1, row + 1)?.takeIf { isCellMoveValid(cell, it) }
        } else null
        return listOfNotNull(
            tlNeighbor, trNeighbor, blNeighbor, brNeighbor,
            tDiagonalNeighbor, rDiagonalNeighbor, bDiagonalNeighbor, lDiagonalNeighbor
        )
    }

    private fun isCellMoveValid(cell: DofusCell, neighbor: DofusCell): Boolean {
        return !neighbor.cellData.nonWalkableDuringRP
                && neighbor.cellData.mov
                && neighbor.cellData.linkedZone != 0
                && abs(cell.cellData.floor - neighbor.cellData.floor) <= 50
    }

    protected abstract fun getDefaultMoveCell(): Int

    protected abstract fun getOverrideX(): Float?

    protected abstract fun getOverrideY(): Float?

    protected abstract fun isCellOk(cellData: CellData): Boolean

}