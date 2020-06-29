package fr.lewon.dofus.bot.scripts.tasks

import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem

abstract class DofusBotTask<T>(
    protected val controller: DofusTreasureBotGUIController,
    private val parentLogItem: LogItem?
) {

    protected abstract fun execute(logItem: LogItem): T

    protected abstract fun onFailed(exception: Exception, logItem: LogItem)

    protected abstract fun onSucceeded(value: T, logItem: LogItem)

    protected abstract fun onStarted(parentLogItem: LogItem?): LogItem

    fun run(): T {
        val logItem = onStarted(parentLogItem)
        try {
            val result = execute(logItem)
            onSucceeded(result, logItem)
            return result
        } catch (e: Exception) {
            onFailed(e, logItem)
            throw e
        }
    }
}