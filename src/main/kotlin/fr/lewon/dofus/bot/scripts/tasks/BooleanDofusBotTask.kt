package fr.lewon.dofus.bot.scripts.tasks

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.util.network.GameInfo

abstract class BooleanDofusBotTask : DofusBotTask<Boolean>() {

    private var error: Throwable? = null

    override fun shouldClearSubLogItems(result: Boolean): Boolean {
        return result
    }

    override fun execute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        error = null
        return try {
            doExecute(logItem, gameInfo)
        } catch (e: InterruptedException) {
            throw e
        } catch (e: IllegalMonitorStateException) {
            throw e
        } catch (e: Throwable) {
            e.printStackTrace()
            error = e
            false
        }
    }

    protected abstract fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean

    override fun onSucceeded(value: Boolean): String {
        return if (value) {
            super.onSucceeded(value)
        } else {
            error?.let { super.onFailed(it) } ?: "KO"
        }
    }

}