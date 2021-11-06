package fr.lewon.dofus.bot.scripts.tasks.impl.complex

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.util.game.MoveUtil

class MultimapMoveTask(private val direction: Direction, private val dist: Int) : BooleanDofusBotTask() {

    override fun execute(logItem: LogItem): Boolean {
        for (i in 0 until dist) {
            VldbLogger.closeLog("Moves done : $i/$dist", logItem)
            if (!MoveUtil.buildMoveTask(direction).run(logItem)) {
                return false
            }
        }
        return true
    }

    override fun onStarted(): String {
        return "Moving [$dist] cells in the [$direction] direction ..."
    }

}