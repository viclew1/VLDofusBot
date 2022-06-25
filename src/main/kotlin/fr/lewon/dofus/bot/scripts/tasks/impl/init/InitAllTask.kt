package fr.lewon.dofus.bot.scripts.tasks.impl.init

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.gui2.util.AppInfo
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.hunt.RefreshHuntTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.LeaveHavenBagTask
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

class InitAllTask : DofusBotTask<Any?>() {

    override fun execute(logItem: LogItem, gameInfo: GameInfo): Any? {
        if (TreasureHuntUtil.isHuntPresent(gameInfo)) {
            if (!RefreshHuntTask().run(logItem, gameInfo)) {
                error("Couldn't refresh hunt")
            }
        } else if (!LeaveHavenBagTask().run(logItem, gameInfo)) {
            error("Couldn't reach or leave haven bag")
        }
        return null
    }

    override fun onStarted(): String {
        return "Initializing all needed information for ${AppInfo.APP_NAME} ..."
    }
}