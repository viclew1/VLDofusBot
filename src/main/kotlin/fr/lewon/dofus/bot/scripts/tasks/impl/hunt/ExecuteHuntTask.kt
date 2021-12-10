package fr.lewon.dofus.bot.scripts.tasks.impl.hunt

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.GameInfo

class ExecuteHuntTask : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken): Boolean {
        if (!TreasureHuntUtil.isHuntPresent(gameInfo)) {
            return false
        }
        while (TreasureHuntUtil.isHuntPresent(gameInfo)) {
            val currentStep = TreasureHuntUtil.getTreasureHunt(gameInfo).huntSteps.last()
            if (!TreasureHuntUtil.executeStep(gameInfo, currentStep, logItem, cancellationToken)) {
                return false
            }
            clickSearchIfNeeded(gameInfo, cancellationToken)
        }
        return true
    }

    private fun clickSearchIfNeeded(gameInfo: GameInfo, cancellationToken: CancellationToken) {
        val hunt = gameInfo.treasureHunt
        if (TreasureHuntUtil.isSearchStep(gameInfo) && hunt != null && hunt.huntFlags.size == hunt.huntSteps.size) {
            WaitUtil.sleep(300)
            TreasureHuntUtil.clickSearch(gameInfo, cancellationToken)
        }
    }

    override fun onStarted(): String {
        return "Executing hunt ..."
    }

}