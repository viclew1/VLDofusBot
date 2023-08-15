package fr.lewon.dofus.bot.gui.main.scripts.logs

import fr.lewon.dofus.bot.core.logs.LogItem

data class LogItemUIState(
    val logItem: LogItem,
    val text: String = logItem.toString(),
    val description: String = logItem.description,
)