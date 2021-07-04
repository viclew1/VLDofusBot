package fr.lewon.dofus.bot.scripts.tasks.impl.hunt

import fr.lewon.dofus.bot.gui.LogItem
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil

class ExecuteHuntTask : DofusBotTask<Boolean>() {

    override fun execute(logItem: LogItem): Boolean {
        if (!TreasureHuntUtil.isHuntPresent()) {
            error("No hunt ongoing")
        }
        while (!huntFinished()) {
            val hunt = TreasureHuntUtil.getTreasureHunt()
            TreasureHuntUtil.executeStep(hunt.huntSteps.last(), logItem)
            TreasureHuntUtil.tickFlag(hunt.huntFlags.size)
        }
        TreasureHuntUtil.clickSearch()
        return true
    }

    private fun huntFinished(): Boolean {
        val hunt = TreasureHuntUtil.getTreasureHunt()
        return hunt.huntFlags.size == hunt.huntSteps.size
    }

    override fun onStarted(): String {
        return "Executing hunt ..."
    }

}