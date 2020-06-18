package fr.lewon.dofus.bot.ui.logic

import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem

abstract class DofusBotTask<T>(
    protected val controller: DofusTreasureBotGUIController,
    private val parentLogItem: LogItem?
) {

    abstract fun execute(logItem: LogItem): T

    abstract fun onFailed(exception: Exception, logItem: LogItem)

    abstract fun onSucceeded(value: T, logItem: LogItem)

    abstract fun onStarted(parentLogItem: LogItem?): LogItem

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