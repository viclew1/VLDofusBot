package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.logs

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.model.characters.DofusCharacter

object LogsUIState {

    private val loggerTypeByCharacter =
        HashMap<DofusCharacter, MutableState<LoggerUIType>>()
    private val autoScrollByLoggerTypeByCharacter =
        HashMap<DofusCharacter, HashMap<LoggerUIType, MutableState<Boolean>>>()
    private val pauseLogsByLoggerTypeByCharacter =
        HashMap<DofusCharacter, HashMap<LoggerUIType, MutableState<Boolean>>>()
    private val logsByLoggerTypeByCharacter =
        HashMap<DofusCharacter, HashMap<LoggerUIType, MutableState<List<LogItem>>>>()
    private val scrollStateByLoggerTypeByCharacter =
        HashMap<DofusCharacter, HashMap<LoggerUIType, MutableState<ScrollState>>>()
    private val expandedLogItemsByLoggerTypeByCharacter =
        HashMap<DofusCharacter, HashMap<LoggerUIType, MutableState<List<LogItem>>>>()
    val logsSelectedCharacter = mutableStateOf("")

    @Synchronized
    fun getLoggerType(character: DofusCharacter): MutableState<LoggerUIType> {
        return loggerTypeByCharacter.computeIfAbsent(character) { mutableStateOf(LoggerUIType.EXECUTION) }
    }

    @Synchronized
    fun getAutoScrollEnabled(character: DofusCharacter, loggerType: LoggerUIType): MutableState<Boolean> {
        return autoScrollByLoggerTypeByCharacter.computeIfAbsent(character) { HashMap() }
            .computeIfAbsent(loggerType) { mutableStateOf(true) }
    }

    @Synchronized
    fun getPauseEnabled(character: DofusCharacter, loggerType: LoggerUIType): MutableState<Boolean> {
        return pauseLogsByLoggerTypeByCharacter.computeIfAbsent(character) { HashMap() }
            .computeIfAbsent(loggerType) { mutableStateOf(false) }
    }

    @Synchronized
    fun getLogs(character: DofusCharacter, loggerType: LoggerUIType): MutableState<List<LogItem>> {
        return logsByLoggerTypeByCharacter.computeIfAbsent(character) { HashMap() }
            .computeIfAbsent(loggerType) { mutableStateOf(emptyList()) }
    }

    @Synchronized
    fun getExpandedLogItems(character: DofusCharacter, loggerType: LoggerUIType): MutableState<List<LogItem>> {
        return expandedLogItemsByLoggerTypeByCharacter.computeIfAbsent(character) { HashMap() }
            .computeIfAbsent(loggerType) { mutableStateOf(emptyList()) }
    }

    @Synchronized
    fun getScrollState(character: DofusCharacter, loggerType: LoggerUIType): MutableState<ScrollState> {
        return scrollStateByLoggerTypeByCharacter.computeIfAbsent(character) { HashMap() }
            .computeIfAbsent(loggerType) { mutableStateOf(ScrollState(0)) }
    }
}