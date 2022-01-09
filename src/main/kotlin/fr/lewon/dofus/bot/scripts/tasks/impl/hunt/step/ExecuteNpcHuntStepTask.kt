package fr.lewon.dofus.bot.scripts.tasks.impl.hunt.step

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFollowDirectionToHint
import fr.lewon.dofus.bot.util.game.MousePositionsUtil
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.network.GameInfo

class ExecuteNpcHuntStepTask(private val huntStep: TreasureHuntStepFollowDirectionToHint) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        for (i in 0 until 10) {
            val moveTask = MoveUtil.buildDirectionalMoveTask(gameInfo, huntStep.direction)
            if (!moveTask.run(logItem, gameInfo)) {
                return false
            }
            if (TreasureHuntUtil.getTreasureHunt(gameInfo).huntFlags.firstOrNull { it.map == gameInfo.currentMap } == null && gameInfo.drhellerOnMap) {
                val flagIndex = TreasureHuntUtil.getTreasureHunt(gameInfo).huntFlags.size
                TreasureHuntUtil.tickFlag(gameInfo, flagIndex)
                MouseUtil.leftClick(gameInfo, MousePositionsUtil.getRestPosition(gameInfo))
                return true
            }
        }
        return false
    }

    override fun onStarted(): String {
        return "Hunt step : [${huntStep.direction}] - Drheller ..."
    }

}