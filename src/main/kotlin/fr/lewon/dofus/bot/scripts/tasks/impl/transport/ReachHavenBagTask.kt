package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.network.GameInfo

class ReachHavenBagTask : AbstractHavenBagTask(true) {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        while (!super.doExecute(logItem, gameInfo)) {
            val lastMove = gameInfo.moveHistory.pollLastMove() ?: return false
            val moveTask = MoveUtil.buildDirectionalMoveTask(gameInfo, lastMove.direction.getReverseDir())
            if (!moveTask.run(logItem, gameInfo)) {
                return false
            }
            gameInfo.moveHistory.pollLastMove()
        }
        return true
    }

    override fun onStarted(): String {
        return "Reaching haven bag ..."
    }
}