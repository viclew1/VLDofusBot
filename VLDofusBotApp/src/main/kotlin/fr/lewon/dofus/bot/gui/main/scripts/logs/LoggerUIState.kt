package fr.lewon.dofus.bot.gui.main.scripts.logs

import androidx.compose.foundation.lazy.LazyListState

data class LoggerUIState(
    val loggerType: LoggerUIType,
    val autoScroll: Boolean = true,
    val pauseLogs: Boolean = false,
    val logItems: List<LogItemUIState> = emptyList(),
    val expandedLogItem: LogItemUIState? = null,
    val listState: LazyListState = LazyListState(),
)