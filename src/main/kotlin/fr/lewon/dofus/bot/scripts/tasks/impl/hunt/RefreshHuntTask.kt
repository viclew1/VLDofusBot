package fr.lewon.dofus.bot.scripts.tasks.impl.hunt

import fr.lewon.dofus.bot.gui.LogItem
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.LeaveHavenBagTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ReachHavenBagTask
import fr.lewon.dofus.bot.sniffer.model.messages.TreasureHuntMessage
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.ui.Debugger

class RefreshHuntTask : DofusBotTask<TreasureHuntMessage>() {

    override fun execute(logItem: LogItem): TreasureHuntMessage {
        if (TreasureHuntUtil.isHuntPresent()) {
            ReachHavenBagTask().run(logItem)
            TreasureHuntUtil.tickFlag(0)
            WaitUtil.sleep(1000)
            val hunt = TreasureHuntUtil.tickFlag(0)
            LeaveHavenBagTask().run(logItem)
            Debugger.debug("Hunt refreshed")
            return hunt
        }
        error("No hunt ongoing")
    }

    override fun onStarted(): String {
        return "Refreshing current hunt ..."
    }
}