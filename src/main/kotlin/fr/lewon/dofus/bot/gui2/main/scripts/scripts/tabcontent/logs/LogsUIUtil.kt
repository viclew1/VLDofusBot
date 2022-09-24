package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.logs

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.model.characters.DofusCharacter

object LogsUIUtil {

    private val logsUIStateByCharacter = HashMap<DofusCharacter, MutableState<CharacterLogsUIState>>()
    private val loggerUIStateByLoggerTypeByCharacter =
        HashMap<DofusCharacter, HashMap<LoggerUIType, MutableState<LoggerUIState>>>()
    val logsSelectedCharacter = mutableStateOf("")

    @Synchronized
    fun getCharacterLogsUIState(character: DofusCharacter): MutableState<CharacterLogsUIState> {
        return logsUIStateByCharacter.computeIfAbsent(character) {
            mutableStateOf(CharacterLogsUIState(it, LoggerUIType.EXECUTION))
        }
    }

    @Synchronized
    fun getLoggerUIState(character: DofusCharacter, loggerType: LoggerUIType): MutableState<LoggerUIState> {
        val loggerUIStateByLoggerType = loggerUIStateByLoggerTypeByCharacter.computeIfAbsent(character) { HashMap() }
        return loggerUIStateByLoggerType.computeIfAbsent(loggerType) { mutableStateOf(LoggerUIState(loggerType)) }
    }

}