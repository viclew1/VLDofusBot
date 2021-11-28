package fr.lewon.dofus.bot.scripts.tasks.impl.windows

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.util.JNAUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.GameInfo
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

class CloseGameTask : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken): Boolean {
        if (isCharacterLoggedIn(gameInfo)) {
            VldbLogger.info("Game already closed.", logItem)
            return true
        }
        JNAUtil.closeGame(gameInfo.pid)
        return WaitUtil.waitUntil({ isCharacterLoggedIn(gameInfo) }, cancellationToken)
    }

    private fun isCharacterLoggedIn(gameInfo: GameInfo): Boolean {
        return GameSnifferUtil.getCharacterPID(gameInfo.character) == null
    }

    override fun onStarted(): String {
        return "Closing game ..."
    }

}