package fr.lewon.dofus.bot.scripts.tasks.impl.hunt

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.LeaveHavenBagTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ReachHavenBagTask
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil
import fr.lewon.dofus.bot.util.io.WaitUtil

class RefreshHuntTask : BooleanDofusBotTask() {

    override fun execute(logItem: LogItem): Boolean {
        if (TreasureHuntUtil.isSearchStep()) {
            ReachHavenBagTask().run(logItem)
            val lastNonTickedIndex = TreasureHuntUtil.getLastNonTickedFlagIndex()
            if (lastNonTickedIndex != null) {
                TreasureHuntUtil.tickFlag(lastNonTickedIndex)
                WaitUtil.sleep(1000)
                TreasureHuntUtil.tickFlag(lastNonTickedIndex)
            } else {
                TreasureHuntUtil.tickFlag(TreasureHuntUtil.flagsCount - 1)
            }
            LeaveHavenBagTask().run(logItem)
            return true
        } else if (TreasureHuntUtil.isFightStep()) {
            return true
        }
        return false
    }

    override fun onStarted(): String {
        return "Refreshing current hunt ..."
    }
}