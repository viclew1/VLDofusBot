package fr.lewon.dofus.bot.scripts.tasks.impl.moves.custom

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.network.GameInfo

class CellMoveTask(private val cellId: Int) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken): Boolean {
        val cellBounds = gameInfo.dofusBoard.getCell(cellId).bounds
        val cellCenter = cellBounds.getCenter()

        val dxMultiplier = if (cellCenter.x > 0.5) 1 else -1
        val clickLocation = PointRelative(
            cellCenter.x + dxMultiplier * cellBounds.width * 0.8f,
            cellCenter.y
        )
        return MoveUtil.processMove(gameInfo, clickLocation, cancellationToken)
    }

    override fun onStarted(): String {
        return "Moving using cell [$cellId] ..."
    }
}