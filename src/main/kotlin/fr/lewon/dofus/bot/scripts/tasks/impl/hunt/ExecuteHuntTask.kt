package fr.lewon.dofus.bot.scripts.tasks.impl.hunt

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil
import fr.lewon.dofus.bot.util.io.WaitUtil

class ExecuteHuntTask : DofusBotTask<Boolean>() {

    override fun execute(logItem: LogItem): Boolean {
        if (!TreasureHuntUtil.isHuntPresent()) {
            return false
        }
        while (!huntFinished()) {
            TreasureHuntUtil.executeStep(TreasureHuntUtil.getTreasureHunt().huntSteps.last(), logItem)
        }
        if (GameInfo.treasureHunt != null) {
            WaitUtil.sleep(300)
            TreasureHuntUtil.clickSearch()
        }
        return true
    }

    private fun huntFinished(): Boolean {
        val hunt = GameInfo.treasureHunt ?: return true
        return hunt.huntFlags.size == hunt.huntSteps.size
    }

    override fun onStarted(): String {
        return "Executing hunt ..."
    }

}