package fr.lewon.dofus.bot.scripts.tasks.impl.hunt.step

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFollowDirectionToHint
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil

class ExecuteNpcHuntStepTask(private val huntStep: TreasureHuntStepFollowDirectionToHint) : BooleanDofusBotTask() {

    override fun execute(logItem: LogItem): Boolean {
        val moveTask = MoveUtil.buildMoveTask(huntStep.direction)
        for (i in 0 until 10) {
            moveTask.run(logItem)
            if (TreasureHuntUtil.getTreasureHunt().huntFlags.firstOrNull { it.map == GameInfo.currentMap } == null && GameInfo.phorrorOnMap) {
                TreasureHuntUtil.tickFlag(TreasureHuntUtil.getTreasureHunt().huntFlags.size)
                return true
            }
        }
        return false
    }

    override fun onStarted(): String {
        return "Executing hunt step : looking for phorror"
    }

}