package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.network.GameInfo

class MultimapMoveTask(private val direction: Direction, private val dist: Int) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        for (i in 0 until dist) {
            gameInfo.logger.closeLog("Moves done : $i/$dist", logItem)
            if (!MoveUtil.buildMoveTask(direction).run(logItem, gameInfo)) {
                return false
            }
        }
        return true
    }

    override fun onStarted(): String {
        return "Moving [$dist] cells in the [$direction] direction ..."
    }

}