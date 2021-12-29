package fr.lewon.dofus.bot.scripts.tasks.impl.moves.custom

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.util.game.InteractiveUtil
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.network.GameInfo

class InteractiveMoveTask(private val elementId: Int) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        return MoveUtil.processMove(gameInfo, InteractiveUtil.getElementClickPosition(gameInfo, elementId))
    }

    override fun onStarted(): String {
        return "Moving using element [$elementId] ..."
    }
}