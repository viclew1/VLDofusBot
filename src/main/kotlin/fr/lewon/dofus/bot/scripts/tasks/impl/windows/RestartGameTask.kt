package fr.lewon.dofus.bot.scripts.tasks.impl.windows

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.util.network.GameInfo

class RestartGameTask : DofusBotTask<DofusConnection>() {

    override fun execute(logItem: LogItem, gameInfo: GameInfo): DofusConnection {
        if (!CloseGameTask().run(logItem, gameInfo)) {
            error("Couldn't close game")
        }
        return OpenGameTask().run(logItem, gameInfo)
    }

    override fun onStarted(): String {
        return "Restarting game ..."
    }

}