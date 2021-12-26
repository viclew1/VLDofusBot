package fr.lewon.dofus.bot.scripts.tasks.impl.windows

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.jna.JNAUtil
import fr.lewon.dofus.bot.util.network.GameInfo
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

class CloseGameTask : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        if (isCharacterLoggedOut(gameInfo)) {
            gameInfo.logger.addSubLog("Game already closed.", logItem)
            return true
        }
        JNAUtil.closeGame(gameInfo.connection.pid)
        return WaitUtil.waitUntil({ isCharacterLoggedOut(gameInfo) })
    }

    private fun isCharacterLoggedOut(gameInfo: GameInfo): Boolean {
        GameSnifferUtil.updateNetwork()
        return GameSnifferUtil.getConnection(gameInfo.character) == null
    }

    override fun onStarted(): String {
        return "Closing game ..."
    }

}