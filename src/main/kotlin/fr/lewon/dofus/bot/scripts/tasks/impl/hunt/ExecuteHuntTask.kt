package fr.lewon.dofus.bot.scripts.tasks.impl.hunt

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil
import fr.lewon.dofus.bot.util.io.WaitUtil

class ExecuteHuntTask : BooleanDofusBotTask() {

    override fun execute(logItem: LogItem): Boolean {
        if (!TreasureHuntUtil.isHuntPresent()) {
            return false
        }
        while (TreasureHuntUtil.isHuntPresent()) {
            if (!TreasureHuntUtil.executeStep(TreasureHuntUtil.getTreasureHunt().huntSteps.last(), logItem)) {
                return false
            }
            clickSearchIfNeeded()
        }
        return true
    }

    private fun clickSearchIfNeeded() {
        val hunt = GameInfo.treasureHunt
        if (TreasureHuntUtil.isSearchStep() && hunt != null && hunt.huntFlags.size == hunt.huntSteps.size) {
            WaitUtil.sleep(300)
            TreasureHuntUtil.clickSearch()
        }
    }

    override fun onStarted(): String {
        return "Executing hunt ..."
    }

}