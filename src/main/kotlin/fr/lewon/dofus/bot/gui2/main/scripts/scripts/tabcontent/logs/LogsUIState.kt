package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.logs

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.model.characters.DofusCharacter

object LogsUIState {

    private val loggerTypeByCharacter =
        HashMap<DofusCharacter, MutableState<LoggerType>>()
    private val autoScrollByLoggerTypeByCharacter =
        HashMap<DofusCharacter, HashMap<LoggerType, MutableState<Boolean>>>()
    private val pauseLogsByLoggerTypeByCharacter =
        HashMap<DofusCharacter, HashMap<LoggerType, MutableState<Boolean>>>()
    private val logsByLoggerTypeByCharacter =
        HashMap<DofusCharacter, HashMap<LoggerType, MutableState<List<LogItem>>>>()
    private val scrollStateByLoggerTypeByCharacter =
        HashMap<DofusCharacter, HashMap<LoggerType, MutableState<ScrollState>>>()
    private val expandedLogItemsByLoggerTypeByCharacter =
        HashMap<DofusCharacter, HashMap<LoggerType, MutableState<List<LogItem>>>>()
    val logsSelectedCharacter = mutableStateOf("")

    @Synchronized
    fun getLoggerType(character: DofusCharacter): MutableState<LoggerType> {
        return loggerTypeByCharacter.computeIfAbsent(character) { mutableStateOf(LoggerType.EXECUTION) }
    }

    @Synchronized
    fun getAutoScrollEnabled(character: DofusCharacter, loggerType: LoggerType): MutableState<Boolean> {
        return autoScrollByLoggerTypeByCharacter.computeIfAbsent(character) { HashMap() }
            .computeIfAbsent(loggerType) { mutableStateOf(true) }
    }

    @Synchronized
    fun getPauseEnabled(character: DofusCharacter, loggerType: LoggerType): MutableState<Boolean> {
        return pauseLogsByLoggerTypeByCharacter.computeIfAbsent(character) { HashMap() }
            .computeIfAbsent(loggerType) { mutableStateOf(false) }
    }

    @Synchronized
    fun getLogs(character: DofusCharacter, loggerType: LoggerType): MutableState<List<LogItem>> {
        return logsByLoggerTypeByCharacter.computeIfAbsent(character) { HashMap() }
            .computeIfAbsent(loggerType) { mutableStateOf(emptyList()) }
    }

    @Synchronized
    fun getExpandedLogItems(character: DofusCharacter, loggerType: LoggerType): MutableState<List<LogItem>> {
        return expandedLogItemsByLoggerTypeByCharacter.computeIfAbsent(character) { HashMap() }
            .computeIfAbsent(loggerType) { mutableStateOf(emptyList()) }
    }

    @Synchronized
    fun getScrollState(character: DofusCharacter, loggerType: LoggerType): MutableState<ScrollState> {
        return scrollStateByLoggerTypeByCharacter.computeIfAbsent(character) { HashMap() }
            .computeIfAbsent(loggerType) { mutableStateOf(ScrollState(0)) }
    }

    enum class LoggerType(
        val label: String,
        val canBePaused: Boolean,
        val loggerGetter: (DofusCharacter) -> VldbLogger
    ) {
        EXECUTION("Execution logs", false, { it.executionLogger }),
        SNIFFER("Sniffer logs", true, { it.snifferLogger })
    }
}