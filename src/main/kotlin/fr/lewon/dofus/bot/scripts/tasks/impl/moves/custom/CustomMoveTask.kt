package fr.lewon.dofus.bot.scripts.tasks.impl.moves.custom

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.network.GameInfo

class CustomMoveTask(private val location: PointRelative) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken): Boolean {
        return MoveUtil.processMove(gameInfo, location, cancellationToken)
    }

    override fun onStarted(): String {
        return "Moving to point [${location.x}, ${location.y}]"
    }
}