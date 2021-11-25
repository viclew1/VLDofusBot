package fr.lewon.dofus.bot.scripts.tasks.impl.hunt.step

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFollowDirectionToHint
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil
import fr.lewon.dofus.bot.util.network.GameInfo

class ExecuteNpcHuntStepTask(private val huntStep: TreasureHuntStepFollowDirectionToHint) : BooleanDofusBotTask() {

    override fun execute(logItem: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken): Boolean {
        val moveTask = MoveUtil.buildMoveTask(huntStep.direction)
        for (i in 0 until 10) {
            if (!moveTask.run(logItem, gameInfo, cancellationToken)) {
                return false
            }
            if (TreasureHuntUtil.getTreasureHunt(gameInfo).huntFlags.firstOrNull { it.map == gameInfo.currentMap } == null && gameInfo.drhellerOnMap) {
                val flagIndex = TreasureHuntUtil.getTreasureHunt(gameInfo).huntFlags.size
                TreasureHuntUtil.tickFlag(gameInfo, flagIndex, cancellationToken)
                return true
            }
        }
        return false
    }

    override fun onStarted(): String {
        return "Executing hunt step : looking for drheller"
    }

}