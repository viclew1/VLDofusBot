package fr.lewon.dofus.bot.scripts.tasks

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.util.network.GameInfo

abstract class DofusBotTask<T> {

    protected abstract fun execute(logItem: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken): T

    protected open fun onFailed(error: Throwable): String {
        return "KO - [${error.localizedMessage}]"
    }

    protected open fun onSucceeded(value: T): String {
        return "OK"
    }

    protected abstract fun onStarted(): String

    fun run(parentLogItem: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken): T {
        val logItem = VldbLogger.info(onStarted(), parentLogItem)
        try {
            cancellationToken.checkCancel()
            val result = execute(logItem, gameInfo, cancellationToken)
            VldbLogger.closeLog(onSucceeded(result), logItem)
            return result
        } catch (e: Throwable) {
            VldbLogger.closeLog(onFailed(e), logItem)
            throw e
        }
    }
}