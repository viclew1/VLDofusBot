package fr.lewon.dofus.bot.scripts.tasks

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.logs.VldbLogger

abstract class DofusBotTask<T> {

    protected abstract fun execute(logItem: LogItem): T

    protected open fun onFailed(exception: Exception): String {
        return "KO - [${exception.localizedMessage}]"
    }

    protected open fun onSucceeded(value: T): String {
        return "OK"
    }

    protected abstract fun onStarted(): String

    fun run(parentLogItem: LogItem?): T {
        val logItem = VldbLogger.info(onStarted(), parentLogItem)
        try {
            val result = execute(logItem)
            VldbLogger.closeLog(onSucceeded(result), logItem)
            return result
        } catch (e: Exception) {
            VldbLogger.closeLog(onFailed(e), logItem)
            throw e
        }
    }
}