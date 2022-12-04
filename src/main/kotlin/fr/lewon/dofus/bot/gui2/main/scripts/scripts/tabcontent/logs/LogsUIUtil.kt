package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.logs

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.gui2.ComposeUIUtil
import java.util.*

object LogsUIUtil : ComposeUIUtil() {

    private val loggerUIStateByLoggerTypeByCharacterName =
        HashMap<String, EnumMap<LoggerUIType, MutableState<LoggerUIState>>>()
    val currentLoggerUIState = mutableStateOf(CurrentLoggerUIState("", LoggerUIType.EXECUTION))

    @Synchronized
    fun getLoggerUIState(characterName: String, loggerType: LoggerUIType): MutableState<LoggerUIState> {
        val loggerUIStateByLoggerType = loggerUIStateByLoggerTypeByCharacterName
            .computeIfAbsent(characterName) { EnumMap(LoggerUIType::class.java) }
        return loggerUIStateByLoggerType.computeIfAbsent(loggerType) { mutableStateOf(LoggerUIState(loggerType)) }
    }

    fun updateLogItem(characterName: String, loggerUIType: LoggerUIType, logItem: LogItem) {
        val loggerUIState = getLoggerUIState(characterName, loggerUIType)
        if (!loggerUIState.value.pauseLogs) {
            val autoScrollEnabled = loggerUIState.value.autoScroll
            val scrollState = if (autoScrollEnabled) {
                ScrollState(Int.MAX_VALUE)
            } else {
                loggerUIState.value.scrollState
            }
            val newLogItems = loggerUIState.value.logItems.toSet().plus(logItem)
                .sortedBy { it.id }.toMutableList()
            var newExpandedLogItem = loggerUIState.value.expandedLogItem
            while (newLogItems.size > loggerUIType.maxCapacity) {
                val removedLogItem = newLogItems.removeAt(0)
                if (newExpandedLogItem == removedLogItem) {
                    newExpandedLogItem = null
                }
            }
            loggerUIState.value = loggerUIState.value.copy(
                logItems = newLogItems,
                expandedLogItem = newExpandedLogItem,
                scrollState = scrollState
            )
        }
    }

}