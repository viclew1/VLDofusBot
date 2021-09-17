package fr.lewon.dofus.bot.scripts.tasks.impl.hunt

import fr.lewon.dofus.bot.gui.LogItem
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.LeaveHavenBagTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ReachHavenBagTask
import fr.lewon.dofus.bot.sniffer.model.messages.treasurehunt.TreasureHuntMessage
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil
import fr.lewon.dofus.bot.util.io.WaitUtil

class RefreshHuntTask : DofusBotTask<TreasureHuntMessage>() {

    override fun execute(logItem: LogItem): TreasureHuntMessage {
        if (TreasureHuntUtil.isHuntPresent()) {
            TreasureHuntUtil.updatePoints()
            ReachHavenBagTask().run(logItem)
            val lastNonTickedIndex = TreasureHuntUtil.getLastNonTickedFlagIndex()
                ?: error("Leave at least one flag non ticked to refresh the hunt")
            TreasureHuntUtil.tickFlag(lastNonTickedIndex)
            WaitUtil.sleep(1000)
            val hunt = TreasureHuntUtil.tickFlag(lastNonTickedIndex)
            LeaveHavenBagTask().run(logItem)
            return hunt
        }
        error("No hunt ongoing")
    }

    override fun onStarted(): String {
        return "Refreshing current hunt ..."
    }
}