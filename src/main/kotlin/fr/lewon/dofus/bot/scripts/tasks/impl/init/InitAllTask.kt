package fr.lewon.dofus.bot.scripts.tasks.impl.init

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.hunt.RefreshHuntTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.LeaveHavenBagTask
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil

class InitAllTask : DofusBotTask<Any?>() {

    override fun execute(logItem: LogItem): Any? {
        if (TreasureHuntUtil.isHuntPresent()) {
            if (!RefreshHuntTask().run(logItem)) {
                error("Couldn't refresh hunt")
            }
        } else if (!LeaveHavenBagTask().run(logItem)) {
            error("Couldn't reach or leave haven bag")
        }
        return null
    }

    override fun onStarted(): String {
        return "Initializing all needed information for VLDofusBot ..."
    }
}