package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.game.move.MoveHistory
import fr.lewon.dofus.bot.util.game.MoveUtil

class ReachHavenBagTask : AbstractHavenBagTask(true) {

    override fun execute(logItem: LogItem): Boolean {
        while (!super.execute(logItem)) {
            val lastMove = MoveHistory.pollLastMove() ?: error("Failed to reach haven bag")
            if (!MoveUtil.buildMoveTask(lastMove.direction.getReverseDir()).run(logItem)) {
                return false
            }
            MoveHistory.pollLastMove()
        }
        return true
    }

    override fun onStarted(): String {
        return "Reaching haven bag ..."
    }
}