package fr.lewon.dofus.bot.scripts.tasks.impl.hunt

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.LeaveHavenBagTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ReachHavenBagTask
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil
import fr.lewon.dofus.bot.util.network.GameInfo

class RefreshHuntTask : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        if (!ReachHavenBagTask().run(logItem, gameInfo)) {
            return false
        }
        if (TreasureHuntUtil.isSearchStep(gameInfo)) {
            val lastNonTickedIndex = TreasureHuntUtil.getLastNonTickedFlagIndex(gameInfo)
            if (lastNonTickedIndex != null) {
                TreasureHuntUtil.tickFlag(gameInfo, lastNonTickedIndex)
                TreasureHuntUtil.tickFlag(gameInfo, lastNonTickedIndex)
            } else {
                val flagIndex = TreasureHuntUtil.getFlagsCount(gameInfo) - 1
                TreasureHuntUtil.tickFlag(gameInfo, flagIndex)
            }
        } else if (TreasureHuntUtil.isFightStep(gameInfo)) {
            TreasureHuntUtil.clickFightForUpdate(gameInfo)
        }
        return LeaveHavenBagTask().run(logItem, gameInfo)
    }

    override fun onStarted(): String {
        return "Refreshing current hunt ..."
    }
}