package fr.lewon.dofus.bot.scripts.tasks

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.util.network.GameInfo

abstract class DofusBotTask<T> {

    protected abstract fun execute(logItem: LogItem, gameInfo: GameInfo): T

    protected open fun onFailed(error: Throwable): String {
        return "KO - [${error.localizedMessage}]"
    }

    protected open fun onSucceeded(value: T): String {
        return "OK"
    }

    protected abstract fun onStarted(): String

    fun run(parentLogItem: LogItem, gameInfo: GameInfo): T {
        val logItem = gameInfo.logger.addSubLog(onStarted(), parentLogItem)
        try {
            val result = execute(logItem, gameInfo)
            gameInfo.logger.closeLog(onSucceeded(result), logItem)
            return result
        } catch (e: Throwable) {
            gameInfo.logger.closeLog(onFailed(e), logItem)
            throw e
        }
    }
}