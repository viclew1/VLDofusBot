package fr.lewon.dofus.bot.scripts.tasks.impl.hunt

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.LeaveHavenBagTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ReachHavenBagTask
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.GameInfo

class RefreshHuntTask : BooleanDofusBotTask() {

    override fun execute(logItem: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken): Boolean {
        if (TreasureHuntUtil.isSearchStep(gameInfo)) {
            if (!ReachHavenBagTask().run(logItem, gameInfo, cancellationToken)) {
                return false
            }
            val lastNonTickedIndex = TreasureHuntUtil.getLastNonTickedFlagIndex(gameInfo)
            if (lastNonTickedIndex != null) {
                if (!TreasureHuntUtil.tickFlag(gameInfo, lastNonTickedIndex, cancellationToken)) {
                    return false
                }
                WaitUtil.sleep(300)
                if (!TreasureHuntUtil.tickFlag(gameInfo, lastNonTickedIndex, cancellationToken)) {
                    return false
                }
            } else {
                if (!TreasureHuntUtil.tickFlag(gameInfo, TreasureHuntUtil.flagsCount - 1, cancellationToken)) {
                    return false
                }
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