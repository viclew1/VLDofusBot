package fr.lewon.dofus.bot.ui.logic

import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import javafx.concurrent.Task
import javafx.concurrent.WorkerStateEvent

abstract class DofusBotTask<T>(
    protected val controller: DofusTreasureBotGUIController,
    private val parentLogItem: LogItem?
) : Task<T>() {

    private lateinit var logItem: LogItem

    init {
        setOnFailed { onFailed(it, logItem) }
        setOnSucceeded { onSucceeded(it, value, logItem) }
    }

    override fun call(): T {
        logItem = onStarted(parentLogItem)
        return execute(logItem)
    }

    abstract fun execute(logItem: LogItem): T

    abstract fun onFailed(event: WorkerStateEvent, logItem: LogItem)

    abstract fun onSucceeded(event: WorkerStateEvent, value: T, logItem: LogItem)

    abstract fun onStarted(parentLogItem: LogItem?): LogItem

    fun runAndGet(): T {
        run()
        return get()
    }
}