package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.game.move.MoveHistory
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.network.GameInfo

class ReachHavenBagTask : AbstractHavenBagTask(true) {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        while (!super.doExecute(logItem, gameInfo)) {
            val lastMove = MoveHistory.pollLastMove() ?: return false
            if (!MoveUtil.buildMoveTask(lastMove.direction.getReverseDir()).run(logItem, gameInfo)) {
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