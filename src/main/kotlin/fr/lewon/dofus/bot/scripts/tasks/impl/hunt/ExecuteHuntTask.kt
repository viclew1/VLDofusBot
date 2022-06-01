package fr.lewon.dofus.bot.scripts.tasks.impl.hunt

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFight
import fr.lewon.dofus.bot.util.game.MousePositionsUtil
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

class ExecuteHuntTask : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        if (gameInfo.treasureHunt == null) {
            return false
        }
        while (gameInfo.treasureHunt != null) {
            val currentStep = TreasureHuntUtil.getTreasureHunt(gameInfo).huntSteps.last()
            if (!TreasureHuntUtil.executeStep(gameInfo, currentStep, logItem)) {
                return false
            }
            tickFlagIfNeeded(gameInfo)
            clickSearchIfNeeded(gameInfo)
        }
        return true
    }

    private fun tickFlagIfNeeded(gameInfo: GameInfo) {
        val hunt = gameInfo.treasureHunt
        if (hunt != null && hunt.huntSteps.last() !is TreasureHuntStepFight) {
            val flagIndex = TreasureHuntUtil.getTreasureHunt(gameInfo).huntFlags.size
            TreasureHuntUtil.tickFlag(gameInfo, flagIndex)
            MouseUtil.leftClick(gameInfo, MousePositionsUtil.getRestPosition(gameInfo))
        }
    }

    private fun clickSearchIfNeeded(gameInfo: GameInfo) {
        val hunt = gameInfo.treasureHunt
        if (hunt != null && hunt.huntFlags.size == hunt.huntSteps.size && hunt.huntSteps.last() !is TreasureHuntStepFight) {
            WaitUtil.sleep(600)
            TreasureHuntUtil.clickSearch(gameInfo)
            if (gameInfo.treasureHunt?.checkPointCurrent == hunt.checkPointCurrent) {
                error("Step failed")
            }
        }
    }

    override fun onStarted(): String {
        return "Executing hunt ..."
    }

}