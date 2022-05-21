package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.d2p.maps.cell.CellData
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.core.ui.UIPoint
import fr.lewon.dofus.bot.core.world.Transition
import fr.lewon.dofus.bot.core.world.TransitionType
import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.ConverterUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.GameInfo
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

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
        if (!doProcessTransition(gameInfo, transition)) {
            return false
        }
        if (gameInfo.currentMap.id != transition.edge.to.mapId) {
            val map = MapManager.getDofusMap(transition.edge.to.mapId)
            error("Movement failed : did not reach expected map (${map.posX}, ${map.posY})")
        }
        return true
    }

    private fun doProcessTransition(gameInfo: GameInfo, transition: Transition): Boolean {
        return when (transition.type) {
            TransitionType.SCROLL, TransitionType.SCROLL_ACTION ->
                processDefaultMove(gameInfo, Direction.fromInt(transition.direction), transition.cellId)
            TransitionType.MAP_ACTION ->
                MoveUtil.processCellMove(gameInfo, transition.cellId)
            TransitionType.INTERACTIVE ->
                MoveUtil.processInteractiveMove(gameInfo, transition.id.toInt(), transition.skillId)
            else -> error("Transition not implemented yet : ${transition.type}")
        }
    }

    private fun processDefaultMove(gameInfo: GameInfo, direction: Direction, linkedZoneCellId: Int): Boolean {
        val moveCellId = getMoveCellId(gameInfo, direction, linkedZoneCellId)
            ?: return false

        val previousMapId = gameInfo.currentMap.id
        val clickLoc = getStandardClickLoc(gameInfo, direction, moveCellId)
        if (!MoveUtil.processMove(gameInfo, clickLoc)) {
            error("Failed to move toward [$direction]")
        }
        gameInfo.moveHistory.addMap(previousMapId)
        return true
    }

    private fun getStandardClickLoc(gameInfo: GameInfo, direction: Direction, destCellId: Int): PointRelative {
        val destCell = gameInfo.dofusBoard.getCell(destCellId)
        val destCellCenter = destCell.getCenter()
        val dFloor = ConverterUtil.toPointRelative(UIPoint(y = destCell.cellData.floor.toFloat()))
        val cellClickLoc = PointRelative(
            max(0.001f, min(destCellCenter.x, 0.99f)),
            max(0.001f, min(destCellCenter.y - dFloor.y, 0.99f))
        )
        return PointRelative(
            getOverrideX(direction) ?: cellClickLoc.x,
            getOverrideY(direction) ?: cellClickLoc.y
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
        if (validMoveCells.isNotEmpty()) {
            getClosestCellId(gameInfo, playerCellId, validMoveCells)?.let {
                return it
            }
        }
        val nextCellIdDelta = getNextCellIdDelta(direction)
        var nextCell = moveCells.maxOrNull() ?: Short.MAX_VALUE.toInt()
        var previousCell = moveCells.minOrNull() ?: -1
        while (nextCell < DofusBoard.MAP_CELLS_COUNT || previousCell >= 0) {
            nextCell += nextCellIdDelta
            previousCell -= nextCellIdDelta
            if (nextCell in 0 until DofusBoard.MAP_CELLS_COUNT && !invalidMoveCells.contains(nextCell)) {
                return nextCell
            }
            if (previousCell in 0 until DofusBoard.MAP_CELLS_COUNT && !invalidMoveCells.contains(previousCell)) {
                return previousCell
            }
        }
        return null
    }

    private fun getNextCellIdDelta(direction: Direction): Int {
        return when (direction) {
            Direction.BOTTOM, Direction.TOP -> 1
            Direction.LEFT, Direction.RIGHT -> 28
        }
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