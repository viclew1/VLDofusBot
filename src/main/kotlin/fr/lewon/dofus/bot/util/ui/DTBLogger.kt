package fr.lewon.dofus.bot.util.ui

import fr.lewon.dofus.bot.gui.LogItem
import java.util.*
import kotlin.collections.ArrayList

object DTBLogger {

    private val logs = Collections.synchronizedList(ArrayList<LogItem>())

    @Synchronized
    private fun updateLogs() {
        //logsTextArea.text = logs.joinToString("\n") + "\n "
        //logsTextArea.scrollTop = Double.MAX_VALUE
    }

    @Synchronized
    fun clearLogs() {
        logs.clear()
        updateLogs()
    }

    @Synchronized
    fun closeLog(message: String, parent: LogItem) {
        parent.closeLog(message)
        updateLogs()
    }

    @Synchronized
    fun appendLog(logItem: LogItem, message: String) {
        logItem.message += message
        updateLogs()
    }

    @Synchronized
    fun log(message: String, parent: LogItem? = null): LogItem {
        val newItem = LogItem(message)
        parent?.addSubItem(newItem) ?: logs.add(newItem)
        while (logs.size >= 5) {
            logs.removeAt(0)
        }
        updateLogs()
        return newItem
    }

}