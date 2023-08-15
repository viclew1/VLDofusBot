package fr.lewon.dofus.bot.gui.main.scripts

import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.gui.ComposeUIUtil
import fr.lewon.dofus.bot.gui.main.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui.main.scripts.parameters.ScriptParametersUIUtil
import fr.lewon.dofus.bot.gui.main.scripts.selector.ScriptSelectorUIUtil
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager
import fr.lewon.dofus.bot.util.script.ScriptRunner
import java.util.concurrent.locks.ReentrantLock

object ScriptsUiUtil : ComposeUIUtil() {

    private val lock = ReentrantLock()
    private val uiState = mutableStateOf(ScriptsUiState())

    fun getUiStateValue() = lock.executeSyncOperation {
        uiState.value
    }

    fun isScriptStarted(): Boolean = lock.executeSyncOperation {
        CharactersUIUtil.getSelectedCharactersUIStates().any { it.runningScript != null }
    }

    fun toggleScript() {
        val isStarted = isScriptStarted()
        Thread {
            ScriptSelectorUIUtil.uiState.value = ScriptSelectorUIUtil.uiState.value.copy(isStartButtonEnabled = false)
            val selectedCharactersNames = CharactersUIUtil.getSelectedCharactersNames()
            val selectedCharacters = CharacterManager.getCharacters(selectedCharactersNames)
            if (isStarted) {
                selectedCharacters.forEach { ScriptRunner.stopScript(it.name) }
            } else {
                val scriptBuilder = getUiStateValue().currentScriptBuilder
                val parameterValues = ScriptParametersUIUtil.getParameterValues(scriptBuilder)
                selectedCharacters.forEach { ScriptRunner.runScript(it, scriptBuilder, parameterValues) }
            }
        }.start()
    }

    fun updateCurrentScriptBuilder(scriptBuilder: DofusBotScriptBuilder) = lock.executeSyncOperation {
        uiState.value = uiState.value.copy(currentScriptBuilder = scriptBuilder)
    }

}