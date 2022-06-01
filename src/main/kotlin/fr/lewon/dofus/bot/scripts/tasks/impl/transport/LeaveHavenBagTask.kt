package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.util.network.info.GameInfo

class LeaveHavenBagTask : AbstractHavenBagTask(false) {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        if (super.doExecute(logItem, gameInfo)) {
            gameInfo.logger.addSubLog("Player ID is : ${gameInfo.playerId}", logItem)
            return true
        }
        return false
    }

    override fun onStarted(): String {
        return "Leaving haven bag ..."
    }
}