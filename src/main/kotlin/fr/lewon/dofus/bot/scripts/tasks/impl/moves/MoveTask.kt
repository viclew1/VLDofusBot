package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.manager.d2p.maps.cell.CellData
import fr.lewon.dofus.bot.core.manager.ui.UIPoint
import fr.lewon.dofus.bot.core.manager.world.Transition
import fr.lewon.dofus.bot.core.manager.world.TransitionType
import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.util.game.InteractiveUtil
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.ConverterUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.GameInfo
import kotlin.math.abs

class MoveTask(private val transitions: List<Transition>) : BooleanDofusBotTask() {

    override fun onStarted(): String {
        return "Moving [${transitions.size}] cell(s) ..."
    }

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        for ((i, transition) in transitions.withIndex()) {
            gameInfo.logger.closeLog("[${getTransitionDescription(transition)}] $i/${transitions.size}", logItem, true)
            if (!processTransition(gameInfo, transition)) {
                return false
            }
        }
        return true
    }

    private fun getTransitionDescription(transition: Transition): String {
        return when (transition.type) {
            TransitionType.SCROLL, TransitionType.SCROLL_ACTION -> Direction.fromInt(transition.direction).toString()
            TransitionType.MAP_ACTION -> "Cell ${transition.cellId}"
            TransitionType.INTERACTIVE -> "Element ${transition.id.toInt()}"
            else -> error("Transition not implemented yet : ${transition.type}")
        }
    }

    private fun processTransition(gameInfo: GameInfo, transition: Transition): Boolean {
        return when (transition.type) {
            TransitionType.SCROLL, TransitionType.SCROLL_ACTION ->
                processDefaultMove(gameInfo, Direction.fromInt(transition.direction), transition.cellId)
            TransitionType.MAP_ACTION ->
                processCellMove(gameInfo, transition.cellId)
            TransitionType.INTERACTIVE ->
                processInteractiveMove(gameInfo, transition.id.toInt())
            else -> error("Transition not implemented yet : ${transition.type}")
        }
    }

    private fun processInteractiveMove(gameInfo: GameInfo, elementId: Int): Boolean {
        return MoveUtil.processMove(gameInfo, InteractiveUtil.getElementClickPosition(gameInfo, elementId))
    }

    private fun processCellMove(gameInfo: GameInfo, cellId: Int): Boolean {
        val cell = gameInfo.dofusBoard.getCell(cellId)
        val cellBounds = cell.bounds
        val cellCenter = cellBounds.getCenter()

        val floor = cell.cellData.floor
        val dxMultiplier = if (floor != 0) 0 else if (cellCenter.x > 0.5) 1 else -1
        val dFloor = ConverterUtil.toPointRelative(UIPoint(y = floor.toFloat()))
        val clickLocation = PointRelative(
            cellCenter.x + dxMultiplier * cellBounds.width * 0.8f,
            cellCenter.y - dFloor.y
        )
        return MoveUtil.processMove(gameInfo, clickLocation)
    }

    private fun processDefaultMove(gameInfo: GameInfo, direction: Direction, linkedZoneCellId: Int): Boolean {
        val fromMap = gameInfo.currentMap
        val moveCellId = getMoveCellId(gameInfo, direction, linkedZoneCellId)
            ?: return false

        val clickLoc = getStandardClickLoc(gameInfo, direction, moveCellId)
        if (!MoveUtil.processMove(gameInfo, clickLoc)) {
            error("Failed to move toward [$direction]")
        }

        gameInfo.moveHistory.addMove(direction, fromMap, gameInfo.currentMap)
        return true
    }

    private fun getStandardClickLoc(gameInfo: GameInfo, direction: Direction, destCellId: Int): PointRelative {
        val destCell = gameInfo.dofusBoard.getCell(destCellId)
        val destCellCenter = destCell.getCenter()
        val floor = if (destCellId == getDefaultMoveCell(direction)) 0 else destCell.cellData.floor
        val dFloor = ConverterUtil.toPointRelative(UIPoint(y = floor.toFloat()))
        return PointRelative(
            getOverrideX(direction) ?: destCellCenter.x,
            getOverrideY(direction) ?: (destCellCenter.y - dFloor.y)
        )
    }

    private fun getMoveCellId(gameInfo: GameInfo, direction: Direction, linkedZoneCellId: Int): Int? {
        var playerCellId = gameInfo.entityPositionsOnMapByEntityId[gameInfo.playerId]
        if (playerCellId == null) {
            WaitUtil.waitUntil({ gameInfo.entityPositionsOnMapByEntityId[gameInfo.playerId] != null })
            playerCellId = gameInfo.entityPositionsOnMapByEntityId[gameInfo.playerId]
                ?: error("No registered position for player : ${gameInfo.playerId}")
        }
        val moveCells = getMoveCellIds(gameInfo, direction, linkedZoneCellId)
        if (moveCells.isEmpty()) {
            return null
        }
        val invalidMoveCells = gameInfo.entityPositionsOnMapByEntityId.values
            .flatMap { getInvalidCellsNearEntity(gameInfo, it) }
            .map { it.cellId }
        val validMoveCells = moveCells
            .filter { !invalidMoveCells.contains(it) }
        if (validMoveCells.isEmpty()) {
            return getDefaultMoveCell(direction)
        }
        return getClosestCellId(gameInfo, playerCellId, validMoveCells)
            ?: getDefaultMoveCell(direction)
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

    private fun getMoveCellIds(gameInfo: GameInfo, direction: Direction, linkedZoneCellId: Int): List<Int> {
        val fromCell = gameInfo.dofusBoard.getCell(linkedZoneCellId)
        val explored = mutableListOf(fromCell.cellId)
        var frontier = listOf(fromCell)
        while (frontier.isNotEmpty()) {
            val newFrontier = ArrayList<DofusCell>()
            for (cell in frontier) {
                for (neighbor in getAccessibleCellsAround(gameInfo, cell)) {
                    if (!explored.contains(neighbor.cellId) && isCellOk(direction, neighbor.cellData)
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

    private fun getDefaultMoveCell(direction: Direction): Int {
        return when (direction) {
            Direction.BOTTOM -> 545
            Direction.LEFT -> 0
            Direction.RIGHT -> DofusBoard.MAP_CELLS_COUNT - 1
            Direction.TOP -> 14
        }
    }

    private fun getOverrideX(direction: Direction): Float? {
        return when (direction) {
            Direction.LEFT -> 0.014948859f
            Direction.RIGHT -> 0.9826908f
            else -> null
        }
    }

    private fun getOverrideY(direction: Direction): Float? {
        return when (direction) {
            Direction.BOTTOM -> 0.8740876f
            Direction.TOP -> 0.0054744524f
            else -> null
        }
    }

    private fun isCellOk(direction: Direction, cellData: CellData): Boolean {
        val mapChangeData = cellData.mapChangeData
        return when (direction) {
            Direction.BOTTOM -> cellData.cellId >= DofusBoard.MAP_CELLS_COUNT - DofusBoard.MAP_WIDTH * 2
                    && (mapChangeData and 2 != 0 || mapChangeData and 4 != 0)
                    && cellData.cellId != 532 && cellData.cellId != 559
            Direction.LEFT -> mapChangeData and 16 != 0
                    || cellData.cellId >= DofusBoard.MAP_WIDTH * 2 && mapChangeData and 32 != 0
                    || cellData.cellId < DofusBoard.MAP_CELLS_COUNT - DofusBoard.MAP_WIDTH * 2 && mapChangeData and 8 != 0
            Direction.RIGHT -> mapChangeData and 1 != 0
                    || cellData.cellId < DofusBoard.MAP_CELLS_COUNT - DofusBoard.MAP_WIDTH * 2 && mapChangeData and 2 != 0
            Direction.TOP -> cellData.cellId < DofusBoard.MAP_WIDTH * 2
                    && (mapChangeData and 32 != 0 || mapChangeData and 64 != 0 || mapChangeData and 128 != 0)
                    && cellData.cellId != 0 && cellData.cellId != 27
        }
    }

}