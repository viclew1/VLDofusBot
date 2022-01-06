package fr.lewon.dofus.bot.scripts.tasks.impl.hunt

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFight
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.GameInfo

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
            clickSearchIfNeeded(gameInfo)
        }
        return true
    }

    private fun clickSearchIfNeeded(gameInfo: GameInfo) {
        val hunt = gameInfo.treasureHunt
        if (hunt != null && hunt.huntFlags.size == hunt.huntSteps.size && hunt.huntSteps.last() !is TreasureHuntStepFight) {
            WaitUtil.sleep(600)
            TreasureHuntUtil.clickSearch(gameInfo)
        }
    }

    override fun onStarted(): String {
        return "Executing hunt ..."
    }

}