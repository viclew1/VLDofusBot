package fr.lewon.dofus.bot.scripts.tasks.impl.windows

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.util.network.GameInfo

class RestartGameTask : DofusBotTask<Long>() {

    override fun execute(logItem: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken): Long {
        if (!CloseGameTask().run(logItem, gameInfo, cancellationToken)) {
            error("Couldn't close game")
        }
        return OpenGameTask().run(logItem, gameInfo, cancellationToken)
    }

    override fun onStarted(): String {
        return "Restarting game ..."
    }

}