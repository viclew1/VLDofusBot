package fr.lewon.dofus.bot.scripts.tasks

import fr.lewon.dofus.bot.gui.LogItem
import fr.lewon.dofus.bot.util.ui.DTBLogger

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
        val logItem = DTBLogger.log(onStarted(), parentLogItem)
        try {
            val result = execute(logItem)
            DTBLogger.closeLog(onSucceeded(result), logItem)
            return result
        } catch (e: Exception) {
            DTBLogger.closeLog(onFailed(e), logItem)
            throw e
        }
    }
}