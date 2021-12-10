package fr.lewon.dofus.bot.scripts.tasks.impl.hunt

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.LeaveHavenBagTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ReachHavenBagTask
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil
import fr.lewon.dofus.bot.util.network.GameInfo

class RefreshHuntTask : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken): Boolean {
        if (TreasureHuntUtil.isSearchStep(gameInfo)) {
            if (!ReachHavenBagTask().run(logItem, gameInfo, cancellationToken)) {
                return false
            }
            val lastNonTickedIndex = TreasureHuntUtil.getLastNonTickedFlagIndex(gameInfo)
            if (lastNonTickedIndex != null) {
                TreasureHuntUtil.tickFlag(gameInfo, lastNonTickedIndex, cancellationToken)
                TreasureHuntUtil.tickFlag(gameInfo, lastNonTickedIndex, cancellationToken)
            } else {
                val flagIndex = TreasureHuntUtil.getFlagsCount(gameInfo) - 1
                TreasureHuntUtil.tickFlag(gameInfo, flagIndex, cancellationToken)
            }
            return LeaveHavenBagTask().run(logItem, gameInfo, cancellationToken)
        } else if (TreasureHuntUtil.isFightStep(gameInfo)) {
            return LeaveHavenBagTask().run(logItem, gameInfo, cancellationToken)
        }
        return false
    }

    override fun onStarted(): String {
        return "Refreshing current hunt ..."
    }
}