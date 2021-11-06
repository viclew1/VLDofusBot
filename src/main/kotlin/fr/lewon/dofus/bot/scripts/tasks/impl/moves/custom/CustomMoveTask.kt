package fr.lewon.dofus.bot.scripts.tasks.impl.moves.custom

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.geometry.PointRelative

class CustomMoveTask(private val location: PointRelative) : BooleanDofusBotTask() {

    override fun execute(logItem: LogItem): Boolean {
        return MoveUtil.processMove(location)
    }

    override fun onStarted(): String {
        return "Moving to point [${location.x}, ${location.y}]"
    }
}