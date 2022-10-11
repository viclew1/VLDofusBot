package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.logs

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

object LogsUIUtil {

    private val logsUIStateByCharacterName = HashMap<String, MutableState<CharacterLogsUIState>>()
    private val loggerUIStateByLoggerTypeByCharacterName =
        HashMap<String, HashMap<LoggerUIType, MutableState<LoggerUIState>>>()
    val logsSelectedCharacter = mutableStateOf("")

    @Synchronized
    fun getCharacterLogsUIState(characterName: String): MutableState<CharacterLogsUIState> {
        return logsUIStateByCharacterName.computeIfAbsent(characterName) {
            mutableStateOf(CharacterLogsUIState(it, LoggerUIType.EXECUTION))
        }
    }

    @Synchronized
    fun getLoggerUIState(characterName: String, loggerType: LoggerUIType): MutableState<LoggerUIState> {
        val loggerUIStateByLoggerType = loggerUIStateByLoggerTypeByCharacterName
            .computeIfAbsent(characterName) { HashMap() }
        return loggerUIStateByLoggerType.computeIfAbsent(loggerType) { mutableStateOf(LoggerUIState(loggerType)) }
    }

}